import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { interval, Subscription } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { Location } from '@angular/common';
import { HeaderComponent } from '../header/header.component';
import { NgChartsModule } from 'ng2-charts';
import { ChartData, ChartOptions } from 'chart.js';
import { ChartConfiguration } from 'chart.js';
import { LightService } from 'src/app/services/light.service';

interface StreetLightReading {
  id: string;
  location: string;
  timestamp: string;
  brightnessLevel: number;
  powerConsumption: number;
  status: 'ON' | 'OFF';

}
type SortableColumn = keyof Pick<StreetLightReading, 'location' | 'timestamp' | 'brightnessLevel' | 'powerConsumption' | 'status'>;



@Component({
  selector: 'app-light-dashboard',
  imports: [CommonModule, FormsModule, HttpClientModule, HeaderComponent, NgChartsModule],
  templateUrl: './light-dashboard.component.html',
  styleUrls: ['./light-dashboard.component.scss']
})
export class LightDashboardComponent implements OnInit {

  //alert banner
  bannerMessage: string | null = null;
  latestAlertTimestamp: string | null = null;
  isAutoRefresh = false;
  isFirstLoad = true;
  private refreshSubscription!: Subscription;



  allData: StreetLightReading[] = [];
  paginatedData: StreetLightReading[] = [];
  allLocations: string[] = [];

  // Filters
  selectedLocation: string = '';
  selectedStatus: string = '';
  fromDate: string = '';
  toDate: string = '';
  sortBy: SortableColumn = 'timestamp';
  pendingSortDirection: 'asc' | 'desc' = 'desc';

  sortDirection: { [key: string]: 'asc' | 'desc' } = {};

  // Pagination
  currentPage = 1;
  pageSize = 10;
  totalPages = 1;
  pages: number[] = [];

  isCollapsed = false;
  filterSummary: string = '';

  chartLabels: string[] = [];


  showVisualizations: boolean = true;

  // Trends
  brightnessTrend: 'up' | 'down' | 'flat' = 'flat';
  powerTrend: 'up' | 'down' | 'flat' = 'flat';

  brightnessChange: number = 0;
  powerChange: number = 0;



  brightnessChartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: [{
      data: [],
      label: 'Brightness Level (%)',
      borderColor: '#3b82f6',         // Tailwind Blue-600
      backgroundColor: '#93c5fd',     // Tailwind Blue-300 (for points)
      pointBackgroundColor: '#3b82f6',
      pointBorderColor: '#fff',
      tension: 0.4,
      borderWidth: 2,
      pointRadius: 4,
      pointHoverRadius: 6,
      fill: false
    }]
  };

  powerChartData: ChartConfiguration<'bar'>['data'] = {
    labels: [],
    datasets: [{
      data: [],
      label: 'Power Consumption (W)',
      backgroundColor: '#10b981',  // Tailwind Emerald-500
      borderColor: '#047857',      // Emerald-700
      borderWidth: 1
    }]
  };

  chartOptions: ChartConfiguration<'line' | 'bar'>['options'] = {
    responsive: true,
    plugins: {
      legend: {
        labels: {
          color: '#374151', // Tailwind gray-700
          font: {
            size: 12,
            weight: 'bold'
          }
        }
      },
      tooltip: {
        backgroundColor: '#1f2937', // dark gray
        titleColor: '#f9fafb',
        bodyColor: '#f9fafb'
      }
    },
    scales: {
      x: {
        ticks: {
          color: '#6b7280' // gray-500
        },
        grid: {
          color: '#e5e7eb' // light gray
        }
      },
      y: {
        ticks: {
          color: '#6b7280'
        },
        grid: {
          color: '#f3f4f6'
        }
      }
    }
  };
  toggleVisualizations(): void {
    this.showVisualizations = !this.showVisualizations;
  }


  constructor(private lightService: LightService, private toastr: ToastrService) {}


  ngOnInit(): void {
    // this.loadMockData();
    // this.applyFilters();
    this.fetchLocations();
    this.fetchDataFromBackend();
    this.refreshSubscription = interval(60000).subscribe(() => {
      this.isAutoRefresh = true;
      this.fetchDataFromBackend();
    });
  }

  ngOnDestroy(): void {
    if (this.refreshSubscription) {
      this.refreshSubscription.unsubscribe();
    }
  }

  fetchLocations(): void {
    this.lightService.getAllLocations().subscribe({
      next: (locations) => this.allLocations = locations,
      error: () => this.toastr.error('Failed to load locations')
    });
  }

  fetchDataFromBackend(): void {
    this.lightService.getReadingsWithAlerts({
      location: this.selectedLocation,
      status: this.selectedStatus,
      start: this.fromDate ? new Date(this.fromDate).toISOString() : undefined,
end: this.toDate
  ? new Date(new Date(this.toDate).setHours(23, 59, 59, 999)).toISOString()
  : undefined,
      sortBy: this.sortBy || 'timestamp',
      sortDir: this.pendingSortDirection,
      page: this.currentPage - 1,
      size: this.pageSize
    }).subscribe({
      next: (res) => {
        this.paginatedData = res.content;
        this.totalPages = Math.ceil(res.totalElements / this.pageSize);
        this.updatePages();
        this.filterSummary = `Showing ${res.content.length} of ${res.totalElements} result(s)`;
        this.updateCharts(res.content);
        // ✅ Detect new alerts (if alerts are sent in res.alerts)
        const allAlerts = res.content
          .flatMap((r: any) => r.alerts || [])
          .filter((a: any) => a.timestamp);
        const latest = allAlerts
          .filter((a: any) => a.timestamp)
          .sort((a: any, b: any) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime())[0];

        if (this.isAutoRefresh && latest && latest.timestamp !== this.latestAlertTimestamp) {
          this.latestAlertTimestamp = latest.timestamp;
          this.bannerMessage = latest.message;

          setTimeout(() => {
            this.bannerMessage = null;
          }, 5000);
        }

        this.isAutoRefresh = false;
        this.isFirstLoad = false;
      },
      error: () => {
        this.toastr.error('Failed to fetch data');
      }
    });
  }
  updatePages(): void {
    const maxButtons = 3;
    const pages: number[] = [];

    const start = Math.max(this.currentPage - Math.floor(maxButtons / 2), 1);
    const end = Math.min(start + maxButtons - 1, this.totalPages);

    if (start > 1) {
      pages.push(1);
      if (start > 2) {
        pages.push(-1); // -1 represents ellipsis (backward)
      }
    }

    for (let i = start; i <= end; i++) {
      pages.push(i);
    }

    if (end < this.totalPages) {
      if (end < this.totalPages - 1) {
        pages.push(-2); // -2 represents ellipsis (forward)
      }
      pages.push(this.totalPages);
    }

    this.pages = pages;
  }



  applyFilters(): void {
    if ((!this.fromDate && this.toDate) || (this.fromDate && !this.toDate)) {
      this.toastr.error('Both start date and end date must be selected.');
      return;
    }

    const now = new Date();
    const from = new Date(this.fromDate);
    const to = new Date(this.toDate);

    if (from > now) {
      this.toastr.error('Start date cannot be in the future.');
      return;
    }

    if (to > now) {
      this.toastr.error('End date cannot be in the future.');
      return;
    }

    if (to < from) {
      this.toastr.error('End date cannot be before start date.');
      return;
    }


    this.currentPage = 1; // reset to first page on new filters
    this.fetchDataFromBackend();
  }

  resetFilters(): void {
    this.selectedLocation = '';
    this.selectedStatus = '';
    this.fromDate = '';
    this.toDate = '';
    this.sortBy = 'timestamp';
    this.pendingSortDirection = 'desc';
    this.sortDirection = {};
    this.applyFilters();
  }

  goToPage(page: number): void {
    this.currentPage = page;
    this.fetchDataFromBackend();
  }
  prevPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.fetchDataFromBackend();
    }
  }
  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.fetchDataFromBackend();
    }
  }
  toggleCollapse(): void {
    this.isCollapsed = !this.isCollapsed;
  }



  paginate(data: StreetLightReading[]): void {
    const start = (this.currentPage - 1) * this.pageSize;
    const end = start + this.pageSize;
    this.paginatedData = data.slice(start, end);
  }

  sortByColumn(column: string): void {
    const validColumns: SortableColumn[] = ['location', 'timestamp', 'brightnessLevel', 'powerConsumption', 'status'];

    if (!validColumns.includes(column as SortableColumn)) {
      return; // ignore invalid column keys
    }

    const typedColumn = column as SortableColumn;

    if (this.sortBy === typedColumn) {
      this.pendingSortDirection = this.pendingSortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortBy = typedColumn;
      this.pendingSortDirection = 'asc';
    }

    this.applyFilters();
  }

  goBack(): void {
    window.history.back();
  }

  handleEllipsisClick(direction: 'forward' | 'backward'): void {
    if (direction === 'forward') {
      this.goToPage(Math.min(this.currentPage + 2, this.totalPages));
    } else {
      this.goToPage(Math.max(this.currentPage - 2, 1));
    }
  }

  updateCharts(data: StreetLightReading[]): void {
    // Sort from oldest to newest for visual timeline display
    const sorted = [...data].sort((a, b) => new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime());

    this.chartLabels = sorted.map(d => new Date(d.timestamp).toLocaleTimeString());

    this.brightnessChartData.labels = this.chartLabels;
    this.brightnessChartData.datasets[0].data = sorted.map(d => d.brightnessLevel);

    this.powerChartData.labels = this.chartLabels;
    this.powerChartData.datasets[0].data = sorted.map(d => d.powerConsumption);

    const brightnessValues = sorted.map(d => d.brightnessLevel);
    const powerValues = sorted.map(d => d.powerConsumption);

    const lastIndex = brightnessValues.length - 1;
    const lastBrightness = brightnessValues[lastIndex];
    const prevBrightness = brightnessValues[lastIndex - 1] ?? lastBrightness;

    const lastPower = powerValues[lastIndex];
    const prevPower = powerValues[lastIndex - 1] ?? lastPower;

    this.brightnessTrend = lastBrightness > prevBrightness ? 'up' :
      lastBrightness < prevBrightness ? 'down' : 'flat';

    this.powerTrend = lastPower > prevPower ? 'up' :
      lastPower < prevPower ? 'down' : 'flat';

    this.powerChange = prevPower === 0 ? 0 :
      ((lastPower - prevPower) / prevPower) * 100;

    this.brightnessChange = prevBrightness === 0 ? 0 :
      ((lastBrightness - prevBrightness) / prevBrightness) * 100;
  }



}
