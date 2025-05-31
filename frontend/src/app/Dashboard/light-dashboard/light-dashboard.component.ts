import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { interval, Subscription } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { Location } from '@angular/common';
import { HeaderComponent } from '../header/header.component'; // ✅ Import your standalone component
import { NgChartsModule } from 'ng2-charts';
import { ChartData, ChartOptions } from 'chart.js';
import { ChartConfiguration } from 'chart.js';

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
  allData: StreetLightReading[] = [];
  paginatedData: StreetLightReading[] = [];
  allLocations: string[] = [];

  // Filters
  selectedLocation: string = '';
  selectedStatus: string = '';
  fromDate: string = '';
  toDate: string = '';
  sortBy: SortableColumn | '' = '';
  pendingSortDirection: 'asc' | 'desc' = 'asc';
  sortDirection: { [key: string]: 'asc' | 'desc' } = {};

  // Pagination
  currentPage = 1;
  pageSize = 5;
  totalPages = 1;
  pages: number[] = [];

  bannerMessage: string = '';
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


  ngOnInit(): void {
    this.loadMockData();
    this.applyFilters();
  }

  loadMockData(): void {
    const mock: StreetLightReading[] = [
      {
        id: '1',
        location: 'Zone A',
        timestamp: '2025-05-30T01:00:00',
        brightnessLevel: 80,
        powerConsumption: 35.2,
        status: 'ON'
      },
      {
        id: '2',
        location: 'Zone B',
        timestamp: '2025-05-30T02:00:00',
        brightnessLevel: 0,
        powerConsumption: 0,
        status: 'OFF'
      },
      {
        id: '3',
        location: 'Zone C',
        timestamp: '2025-05-29T23:00:00',
        brightnessLevel: 65,
        powerConsumption: 28.5,
        status: 'ON'
      },
      {
        id: '4',
        location: 'Zone A',
        timestamp: '2025-05-29T22:30:00',
        brightnessLevel: 90,
        powerConsumption: 40.1,
        status: 'ON'
      },
      {
        id: '5',
        location: 'Zone D',
        timestamp: '2025-05-28T22:00:00',
        brightnessLevel: 0,
        powerConsumption: 0,
        status: 'OFF'
      }
    ];
    this.allData = mock;
    this.allLocations = [...new Set(mock.map(m => m.location))];
  }

  toggleCollapse(): void {
    this.isCollapsed = !this.isCollapsed;
  }

 applyFilters(): void {
  let filtered = [...this.allData];

  if (this.selectedLocation) {
    filtered = filtered.filter(d => d.location === this.selectedLocation);
  }

  if (this.selectedStatus) {
    filtered = filtered.filter(d => d.status === this.selectedStatus);
  }

  if (this.fromDate) {
    filtered = filtered.filter(d => new Date(d.timestamp) >= new Date(this.fromDate));
  }

  if (this.toDate) {
    filtered = filtered.filter(d => new Date(d.timestamp) <= new Date(this.toDate));
  }

  if (this.sortBy !== '') {
    const key = this.sortBy as SortableColumn;
    const dir = this.pendingSortDirection === 'asc' ? 1 : -1;

    filtered.sort((a, b) => {
      const aVal = a[key];
      const bVal = b[key];
      if (aVal < bVal) return -1 * dir;
      if (aVal > bVal) return 1 * dir;
      return 0;
    });

    this.sortDirection = { [key]: this.pendingSortDirection };
  }

  this.filterSummary = `Showing ${filtered.length} result(s)`;
  this.setupPagination(filtered);
  this.updateCharts(filtered); 

}


  resetFilters(): void {
    this.selectedLocation = '';
    this.selectedStatus = '';
    this.fromDate = '';
    this.toDate = '';
    this.sortBy = '';
    this.pendingSortDirection = 'asc';
    this.sortDirection = {};
    this.applyFilters();
  }

  setupPagination(data: StreetLightReading[]): void {
    this.totalPages = Math.ceil(data.length / this.pageSize);
    this.pages = Array.from({ length: this.totalPages }, (_, i) => i + 1);
    this.currentPage = 1;
    this.paginate(data);
  }

  paginate(data: StreetLightReading[]): void {
    const start = (this.currentPage - 1) * this.pageSize;
    const end = start + this.pageSize;
    this.paginatedData = data.slice(start, end);
  }

  goToPage(page: number): void {
    this.currentPage = page;
    this.paginate(this.getFilteredData());
  }

  prevPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.paginate(this.getFilteredData());
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.paginate(this.getFilteredData());
    }
  }

  getFilteredData(): StreetLightReading[] {
    let data = [...this.allData];

    if (this.selectedLocation) {
      data = data.filter(d => d.location === this.selectedLocation);
    }

    if (this.selectedStatus) {
      data = data.filter(d => d.status === this.selectedStatus);
    }

    if (this.fromDate) {
      data = data.filter(d => new Date(d.timestamp) >= new Date(this.fromDate));
    }

    if (this.toDate) {
      data = data.filter(d => new Date(d.timestamp) <= new Date(this.toDate));
    }

if (this.sortBy !== '') {
  const key = this.sortBy as SortableColumn;
  const dir = this.pendingSortDirection === 'asc' ? 1 : -1;

  data.sort((a: StreetLightReading, b: StreetLightReading) => {
    const aVal = a[key];
    const bVal = b[key];
    if (aVal < bVal) return -1 * dir;
    if (aVal > bVal) return 1 * dir;
    return 0;
  });

  this.sortDirection = { [key]: this.pendingSortDirection };
}



    return data;
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
  const sorted = [...data].sort((a, b) => new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime());

  this.chartLabels = sorted.map(d => new Date(d.timestamp).toLocaleTimeString());

  this.brightnessChartData.labels = this.chartLabels;
  this.brightnessChartData.datasets[0].data = sorted.map(d => d.brightnessLevel);

  this.powerChartData.labels = this.chartLabels;
  this.powerChartData.datasets[0].data = sorted.map(d => d.powerConsumption);

  const brightnessValues = sorted.map(d => d.brightnessLevel);

  const powerValues = sorted.map(d => d.powerConsumption);

  const lastBrightness = brightnessValues[brightnessValues.length - 1];
  const prevBrightness = brightnessValues[brightnessValues.length - 2] ?? lastBrightness;

  const lastPower = powerValues[powerValues.length - 1];
  const prevPower = powerValues[powerValues.length - 2] ?? lastPower;

  this.brightnessTrend = lastBrightness > prevBrightness ? 'up' :
                        lastBrightness < prevBrightness ? 'down' : 'flat';

  this.powerTrend = lastPower > prevPower ? 'up' :
                    lastPower < prevPower ? 'down' : 'flat';

  this.powerChange = prevPower === 0 ? 0 :
                   ((lastPower - prevPower) / prevPower) * 100;

}


}
