import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from '../header/header.component';
import { HttpClientModule } from '@angular/common/http';
import { AirPollutionService, AirPollutionReadingWithAlertDTO } from 'src/app/services/air-pollution.service';
import { interval, Subscription } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { Location } from '@angular/common';
import { ChartConfiguration } from 'chart.js';
import { NgChartsModule } from 'ng2-charts';

interface AirData {
  location: string;
  time: string;
  co: number;
  ozone: number;
  pollutionLevel: string;
  alert?: string;
  alerts?: any[];
}

@Component({
  selector: 'app-air-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    HttpClientModule,
    HeaderComponent,
    NgChartsModule
  ],
  templateUrl: './air-dashboard.component.html',
  styleUrls: ['./air-dashboard.component.scss']
})
export class AirDashboardComponent implements OnInit, OnDestroy {
  private refreshSubscription!: Subscription;
  bannerMessage: string | null = null;
  isFirstLoad = true;
  isAutoRefresh = false;
  latestAlertTimestamp: string | null = null;

  data: AirData[] = [];
  filteredData: AirData[] = [];
  paginatedData: AirData[] = [];

  locations: string[] = [];
  selectedLocation: string = '';
  selectedPollutionLevel: string = '';
  fromDate: string = '';
  toDate: string = '';
  sortBy: string = '';
  selectedSortDirection: 'asc' | 'desc' = 'desc';
  pendingSortDirection: 'asc' | 'desc' = 'desc';
  sortDirection: { [key: string]: 'asc' | 'desc' | undefined } = {};
  currentPage: number = 1;
  pageSize: number = 10;
  totalPages: number = 1;
  pages: number[] = [];

  showVisualizations = false;

  coTrend: 'up' | 'down' | 'flat' = 'flat';
  coChange: number = 0;
  chartLabels: string[] = [];

  coChartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: [{
      data: [],
      label: 'CO (ppm)',
      borderColor: '#6366f1',
      backgroundColor: '#a5b4fc',
      pointBackgroundColor: '#6366f1',
      pointBorderColor: '#fff',
      tension: 0.4,
      borderWidth: 2,
      pointRadius: 4,
      pointHoverRadius: 6,
      fill: false
    }]
  };

  ozoneChartData: ChartConfiguration<'bar'>['data'] = {
    labels: [],
    datasets: [{
      data: [],
      label: 'Ozone (ppb)',
      backgroundColor: '#facc15',
      borderColor: '#b45309',
      borderWidth: 1
    }]
  };

  chartOptions: ChartConfiguration<'line' | 'bar'>['options'] = {
    responsive: true,
    plugins: {
      legend: {
        labels: {
          color: '#374151',
          font: { size: 12, weight: 'bold' }
        }
      },
      tooltip: {
        backgroundColor: '#1f2937',
        titleColor: '#f9fafb',
        bodyColor: '#f9fafb'
      }
    },
    scales: {
      x: {
        ticks: { color: '#6b7280' },
        grid: { color: '#e5e7eb' }
      },
      y: {
        beginAtZero: true,
        ticks: {
          color: '#6b7280',
          stepSize: 10
        },
        grid: { color: '#f3f4f6' }
      }
    }
  };

  constructor(
    private AirPollutionService: AirPollutionService,
    private toastr: ToastrService,
    private location: Location
  ) {}

  allLocations: string[] = [];

  ngOnInit(): void {
    this.fetchAllLocations();
    this.fetchAirData();

    this.refreshSubscription = interval(60000).subscribe(() => {
      this.isAutoRefresh = true;
      this.fetchAirData();
    });
  }

  toggleVisualizations(): void {
    this.showVisualizations = !this.showVisualizations;
  }

  fetchAllLocations(): void {
    this.AirPollutionService.getAllLocations().subscribe((locations: string[]) => {
      this.allLocations = locations;
    });
  }

  fetchAirData(): void {
    this.AirPollutionService.getAirPollutionData({
      location: this.selectedLocation,
      pollutionLevel: this.selectedPollutionLevel,
      start: this.fromDate ? this.fromDate + 'T00:00:00' : undefined,
      end: this.toDate ? this.toDate + 'T23:59:59' : undefined,
      sortBy: this.sortBy === 'alert' ? 'alertTimestamp' : this.sortBy,
      sortDir: this.selectedSortDirection,
      page: this.currentPage - 1,
      size: this.pageSize
    }).subscribe((res: { content: any[]; totalElements: number; }) => {
      const data = res.content.map(d => ({
        location: d.location,
        time: d.timestamp,
        co: d.co,
        ozone: d.ozone,
        pollutionLevel: d.pollutionLevel,
        alert: d.alertTimestamp,
        alerts: d.alerts
      }));

      this.data = data;
      this.filteredData = [...data];
      this.paginatedData = [...data];

      this.totalPages = Math.ceil(res.totalElements / this.pageSize);
      this.pages = this.generatePagination(this.currentPage, this.totalPages);

      this.updateCharts(data);

      const allAlerts = data.flatMap((d: { alerts: any; }) => d.alerts || []).filter(a => a.timestamp);

      const latest = allAlerts.sort((a: { timestamp: string | number | Date; }, b: { timestamp: string | number | Date; }) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime())[0];

      if (this.isAutoRefresh && latest && latest.timestamp !== this.latestAlertTimestamp) {
        this.latestAlertTimestamp = latest.timestamp;
        this.bannerMessage = latest.message || 'New air quality alert received';

        setTimeout(() => {
          this.bannerMessage = null;
        }, 5000);
      }

      this.isAutoRefresh = false;
      this.isFirstLoad = false;
    });
  }

  applyFilters(): void {
    const now = new Date();
    const fromDateObj = this.fromDate ? new Date(this.fromDate) : null;
    const toDateObj = this.toDate ? new Date(this.toDate) : null;

    if ((fromDateObj && fromDateObj > now) || (toDateObj && toDateObj > now)) {
      this.toastr.error('Date cannot be in the future.', 'Invalid Date');
      return;
    }

    if (fromDateObj && toDateObj && toDateObj < fromDateObj) {
      this.toastr.error('End date cannot be before start date.', 'Invalid Date');
      return;
    }

    this.selectedSortDirection = this.pendingSortDirection;
    this.currentPage = 1;
    this.fetchAirData();
  }

  resetFilters(): void {
    this.selectedLocation = '';
    this.selectedPollutionLevel = '';
    this.fromDate = '';
    this.toDate = '';
    this.sortBy = '';
    this.sortDirection = {};
    this.selectedSortDirection = 'desc';
    this.pendingSortDirection = 'desc';
    this.fetchAirData();
  }

  ngOnDestroy(): void {
    if (this.refreshSubscription) {
      this.refreshSubscription.unsubscribe();
    }
  }

  sortByColumn(column: keyof AirData) {
    Object.keys(this.sortDirection).forEach(key => {
      if (key !== column) this.sortDirection[key] = undefined;
    });

    const direction = this.sortDirection[column] === 'asc' ? 'desc' : 'asc';
    this.sortDirection[column] = direction;

    this.sortBy = column;

    this.filteredData.sort((a, b) => {
      let valA = a[column] ?? '';
      let valB = b[column] ?? '';

      if (column === 'time' || column === 'alert') {
        // @ts-ignore
        valA = valA ? new Date(valA).getTime() : (direction === 'asc' ? Number.POSITIVE_INFINITY : Number.NEGATIVE_INFINITY);
        // @ts-ignore
        valB = valB ? new Date(valB).getTime() : (direction === 'asc' ? Number.POSITIVE_INFINITY : Number.NEGATIVE_INFINITY);
      }

      if (valA < valB) return direction === 'asc' ? -1 : 1;
      if (valA > valB) return direction === 'asc' ? 1 : -1;
      return 0;
    });

    this.currentPage = 1;
    this.updatePagination();
  }

  get filterSummary(): string {
    const location = this.selectedLocation || 'All locations';
    const level = this.selectedPollutionLevel || 'All pollution levels';
    const column = this.sortBy || 'no sort';
    const dirLabel = this.pendingSortDirection === 'asc' ? 'ascending' : 'descending';

    return `${location} • ${level} • Sorted by ${column} (${dirLabel})`;
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.fetchAirData();
    }
  }

  nextPage() {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.showVisualizations = false;
      this.fetchAirData();
    }
  }

  prevPage() {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.showVisualizations = false;
      this.fetchAirData();
    }
  }

  handleEllipsisClick(direction: 'forward' | 'backward'): void {
    if (direction === 'forward') {
      this.goToPage(Math.min(this.currentPage + 2, this.totalPages));
    } else {
      this.goToPage(Math.max(this.currentPage - 2, 1));
    }
  }

  updatePagination(): void {
    this.paginatedData = this.filteredData;
    this.pages = this.generatePagination(this.currentPage, this.totalPages);
    if (this.paginatedData.length > 0) {
      this.updateCharts(this.paginatedData);
    }
  }

  generatePagination(current: number, total: number): number[] {
    const maxButtons = 3;
    const pages: number[] = [];

    const start = Math.max(current - Math.floor(maxButtons / 2), 1);
    const end = Math.min(start + maxButtons - 1, total);

    if (start > 1) {
      pages.push(1);
      if (start > 2) {
        pages.push(-1);
      }
    }

    for (let i = start; i <= end; i++) {
      pages.push(i);
    }

    if (end < total) {
      if (end < total - 1) {
        pages.push(-2);
      }
      pages.push(total);
    }

    return pages;
  }

  isCollapsed = true;

  toggleCollapse() {
    this.isCollapsed = !this.isCollapsed;
  }

  goBack(): void {
    this.location.back();
  }

  updateCharts(data: AirData[]): void {
    const sorted = [...data].sort(
      (a, b) => new Date(a.time).getTime() - new Date(b.time).getTime()
    );

    const chartLabels = sorted.map(d => new Date(d.time).toLocaleTimeString());
    const coValues = sorted.map(d => d.co);
    const ozoneValues = sorted.map(d => d.ozone);

    this.coChartData = {
      labels: chartLabels,
      datasets: [{
        ...this.coChartData.datasets[0],
        data: coValues
      }]
    };

    this.ozoneChartData = {
      labels: chartLabels,
      datasets: [{
        ...this.ozoneChartData.datasets[0],
        data: ozoneValues
      }]
    };

    const last = coValues[coValues.length - 1] ?? 0;
    const prev = coValues[coValues.length - 2] ?? last;

    this.coTrend = last > prev ? 'up' : last < prev ? 'down' : 'flat';
    this.coChange = prev === 0 ? 0 : ((last - prev) / prev) * 100;
  }
}
