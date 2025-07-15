import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from '../header/header.component'; // ✅ Import your standalone component
import { AlertComponent } from '../alert/alert.component';
import { HttpClientModule } from '@angular/common/http';
import { TrafficService, TrafficReadingWithAlertDTO } from 'src/app/services/traffic.service';
import { interval, Subscription } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { Location } from '@angular/common';
import { ChartConfiguration } from 'chart.js';
import { NgChartsModule } from 'ng2-charts';

interface TrafficData {
  location: string;
  time: string;
  density: number;
  speed: number;
  congestion: string;
  alert?: string; //  Optional alert timestamp
}

@Component({
  selector: 'app-traffic-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    HttpClientModule,
    HeaderComponent,
    NgChartsModule
    // AlertComponent,
  ],
  templateUrl: './traffic-dashboard.component.html',
  styleUrls: ['./traffic-dashboard.component.scss']
})

export class TrafficDashboardComponent implements OnInit, OnDestroy {
  private refreshSubscription!: Subscription;
  bannerMessage: string | null = null;
  isFirstLoad = true;
  isAutoRefresh = false;
  latestAlertTimestamp: string | null = null; // Add this as a class field


  data: TrafficData[] = [];
  filteredData: TrafficData[] = [];
  paginatedData: TrafficData[] = [];


  locations: string[] = [];
  selectedLocation: string = '';
  selectedCongestion: string = '';
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

speedTrend: 'up' | 'down' | 'flat' = 'flat';
speedChange: number = 0;
chartLabels: string[] = [];

speedChartData: ChartConfiguration<'line'>['data'] = {
  labels: [],
  datasets: [{
    data: [],
    label: 'Avg Speed (km/h)',
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

densityChartData: ChartConfiguration<'bar'>['data'] = {
  labels: [],
  datasets: [{
    data: [],
    label: 'Traffic Density',
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
      stepSize: 10 // 👈 adjust for better scale
    },
    grid: { color: '#f3f4f6' }
  }
}

};



  // constructor(private trafficService: TrafficService) {}

constructor(
  private trafficService: TrafficService,
  private toastr: ToastrService,
  private location: Location // ✅ Add this
) {}
  allLocations: string[] = []; // 🔁 new variable for dropdown

ngOnInit(): void {
  this.fetchAllLocations();
  this.fetchTrafficData(); // Initial load

this.refreshSubscription = interval(60000).subscribe(() => {
  this.isAutoRefresh = true; // ✅ Set flag to true before refresh
  this.fetchTrafficData();
});

}

toggleVisualizations(): void {
  this.showVisualizations = !this.showVisualizations;
}
fetchAllLocations(): void {
  this.trafficService.getAllLocations().subscribe(locations => {
    this.allLocations = locations;
  });
}




  // ngOnInit(): void {
  //   this.fetchTrafficData();
  // }


fetchTrafficData(): void {
  console.log('Fetching traffic data...');

  this.trafficService.getTrafficData({
    location: this.selectedLocation,
    congestionLevel: this.selectedCongestion,
    start: this.fromDate ? this.fromDate + 'T00:00:00' : undefined,
    end: this.toDate ? this.toDate + 'T23:59:59' : undefined,
    sortBy: this.sortBy === 'alert' ? 'alertTimestamp' : this.sortBy,
    sortDir: this.selectedSortDirection,
    page: this.currentPage - 1,
    size: this.pageSize
  }).subscribe(res => {
    console.log('Response received from backend:', res);

    const data = res.content.map(d => ({
      location: d.location,
      time: d.timestamp,
      density: d.trafficDensity,
      speed: d.avgSpeed,
      congestion: d.congestionLevel,
      alert: d.alertTimestamp,
      alerts: d.alerts
    }));

    console.log('[✅] Parsed traffic data:', data);

    this.data = data;
    this.filteredData = [...data];
    this.paginatedData = [...data];

    this.totalPages = Math.ceil(res.totalElements / this.pageSize);
    this.pages = this.generatePagination(this.currentPage, this.totalPages);

    this.updateCharts(data);

    // ALERT BANNER LOGIC STARTS HERE
    console.log('Extracting all alerts from data');
    const allAlerts = data
      .flatMap(d => d.alerts || [])
      .filter(a => a.timestamp);

    console.log('All valid alerts with timestamp:', allAlerts);

    const latest = allAlerts
      .sort((a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime())[0];

    if (latest) {
      console.log('Latest alert timestamp:', latest.timestamp);
    } else {
      console.log('No valid alerts found!');
    }

    if (this.isAutoRefresh && latest && latest.timestamp !== this.latestAlertTimestamp) {
      console.log('New alert detected — showing banner!');
      this.latestAlertTimestamp = latest.timestamp;
      this.bannerMessage = latest.message || 'New traffic alert received';

      setTimeout(() => {
        console.log('Hiding alert banner after 5s.');
        this.bannerMessage = null;
      }, 5000);
    } else {
      if (this.isAutoRefresh) {
        console.log('No new alert — skipping banner update.');
      }
    }

    this.isAutoRefresh = false;
    this.isFirstLoad = false;
  }, err => {
    console.error('Failed to fetch traffic data:', err);
  });
}




applyFilters(): void {
  const now = new Date();
  const fromDateObj = this.fromDate ? new Date(this.fromDate) : null;
  const toDateObj = this.toDate ? new Date(this.toDate) : null;

  // Validate date logic
  if ((fromDateObj && fromDateObj > now) || (toDateObj && toDateObj > now)) {
    this.toastr.error('Date cannot be in the future.', 'Invalid Date');
    return;
  }

  if (fromDateObj && toDateObj && toDateObj < fromDateObj) {
    this.toastr.error('End date cannot be before start date.', 'Invalid Date');
    return;
  }

  // Reset page + apply sort + fetch with filters
  this.selectedSortDirection = this.pendingSortDirection;
  this.currentPage = 1;
  this.fetchTrafficData();
}






resetFilters(): void {
  this.selectedLocation = '';
  this.selectedCongestion = '';
  this.fromDate = '';
  this.toDate = '';
  this.sortBy = ''; //  Clear sort column
  this.sortDirection = {}; //  Clear sort direction map
  this.selectedSortDirection = 'desc';
  this.pendingSortDirection = 'desc';
  this.fetchTrafficData();
}

ngOnDestroy(): void {
  if (this.refreshSubscription) {
    this.refreshSubscription.unsubscribe(); // Prevent memory leaks
  }
}




sortData() {
  if (!this.sortBy) {
    // No sorting applied
    this.updatePagination();
    return;
  }

  const column = this.sortBy as keyof TrafficData;
  const direction = this.selectedSortDirection;

this.filteredData.sort((a, b) => {
  const valA = a.alert ? new Date(a.alert).getTime() : null;
  const valB = b.alert ? new Date(b.alert).getTime() : null;

  if (valA === null && valB === null) return 0;
  if (valA === null) return 1;
  if (valB === null) return -1;

  return this.selectedSortDirection === 'asc' ? valA - valB : valB - valA;
});


  this.updatePagination();
}




goToPage(page: number): void {
  if (page >= 1 && page <= this.totalPages) {
    this.currentPage = page;
    this.fetchTrafficData(); // Backend handles correct pagination
  }
}



updatePagination(): void {
  this.paginatedData = this.filteredData;
  this.pages = this.generatePagination(this.currentPage, this.totalPages);
if (this.paginatedData && this.paginatedData.length > 0) {
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
      pages.push(-1); // backward ellipsis
    }
  }

  for (let i = start; i <= end; i++) {
    pages.push(i);
  }

  if (end < total) {
    if (end < total - 1) {
      pages.push(-2); // forward ellipsis
    }
    pages.push(total);
  }

  return pages;
}








nextPage() {
  if (this.currentPage < this.totalPages) {
    this.currentPage++;
    this.showVisualizations = false;
    this.fetchTrafficData();
  }
}


prevPage() {
  if (this.currentPage > 1) {
    this.currentPage--;
    this.showVisualizations = false;
    this.fetchTrafficData();
  }
}


handleEllipsisClick(direction: 'forward' | 'backward'): void {
  if (direction === 'forward') {
    this.goToPage(Math.min(this.currentPage + 2, this.totalPages));
  } else {
    this.goToPage(Math.max(this.currentPage - 2, 1));
  }
}







get filterSummary(): string {
  const location = this.selectedLocation || 'All locations';
  const congestion = this.selectedCongestion || 'All congestion levels';
  const column = this.sortBy || 'no sort';
  const dirLabel = this.pendingSortDirection === 'asc' ? 'ascending' : 'descending';

  return `${location} • ${congestion} • Sorted by ${column} (${dirLabel})`;
}




sortByColumn(column: keyof TrafficData) {
  // Reset all other directions
  Object.keys(this.sortDirection).forEach(key => {
    if (key !== column) this.sortDirection[key] = undefined;
  });

  const direction = this.sortDirection[column] === 'asc' ? 'desc' : 'asc';
  this.sortDirection[column] = direction;

  this.sortBy = column; // Set the current sort column for summary

  this.filteredData.sort((a, b) => {
    let valA = a[column] ?? '';
    let valB = b[column] ?? '';


    if (column === 'time' || column === 'alert') {
      valA = valA ? new Date(valA).getTime() : (direction === 'asc' ? Number.POSITIVE_INFINITY : Number.NEGATIVE_INFINITY);
      valB = valB ? new Date(valB).getTime() : (direction === 'asc' ? Number.POSITIVE_INFINITY : Number.NEGATIVE_INFINITY);
    }



    if (valA < valB) return direction === 'asc' ? -1 : 1;
    if (valA > valB) return direction === 'asc' ? 1 : -1;
    return 0;
  });

  this.currentPage = 1;
  this.updatePagination();
}


isCollapsed = true;

toggleCollapse() {
  this.isCollapsed = !this.isCollapsed;
}

goBack(): void {
  this.location.back();
}

updateCharts(data: TrafficData[]): void {
  const sorted = [...data].sort(
    (a, b) => new Date(a.time).getTime() - new Date(b.time).getTime()
  );

  const chartLabels = sorted.map(d => new Date(d.time).toLocaleTimeString());
  const avgSpeeds = sorted.map(d => d.speed);
  const trafficDensities = sorted.map(d => d.density);

  // Reassign the entire chart objects (new references)
  this.speedChartData = {
    labels: chartLabels,
    datasets: [{
      ...this.speedChartData.datasets[0], // retain color/style config
      data: avgSpeeds
    }]
  };

  this.densityChartData = {
    labels: chartLabels,
    datasets: [{
      ...this.densityChartData.datasets[0],
      data: trafficDensities
    }]
  };

  // Update trends
  const last = avgSpeeds[avgSpeeds.length - 1] ?? 0;
  const prev = avgSpeeds[avgSpeeds.length - 2] ?? last;

  this.speedTrend = last > prev ? 'up' : last < prev ? 'down' : 'flat';
  this.speedChange = prev === 0 ? 0 : ((last - prev) / prev) * 100;
}





}
