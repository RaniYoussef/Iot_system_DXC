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
  alert?: string; // ✅ Optional alert timestamp
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
  selectedLocation = '';
  selectedCongestion = '';
  fromDate = '';
  toDate = '';
  sortBy = '';
  selectedSortDirection: 'asc' | 'desc' = 'desc';
  pendingSortDirection: 'asc' | 'desc' = 'desc';
  sortDirection: Record<string, 'asc' | 'desc' | undefined> = {};
  currentPage = 1;
  pageSize = 10;
  totalPages = 1;
  pages: number[] = [];


  showVisualizations = false;

speedTrend: 'up' | 'down' | 'flat' = 'flat';
speedChange = 0;
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
  const filters: any = {
    location: this.selectedLocation,
    congestionLevel: this.selectedCongestion
  };

  if (this.fromDate) filters.start = this.fromDate + 'T00:00:00';
  if (this.toDate) filters.end = this.toDate + 'T23:59:59';
  if (this.sortBy) {
    filters.sortBy = this.sortBy === 'alert' ? 'alertTimestamp' : this.sortBy;
    filters.sortDir = this.selectedSortDirection;
  }

this.trafficService.getTrafficData({
  location: this.selectedLocation,
  congestionLevel: this.selectedCongestion,
  start: this.fromDate ? this.fromDate + 'T00:00:00' : undefined,
  end: this.toDate ? this.toDate + 'T23:59:59' : undefined,
  sortBy: this.sortBy === 'alert' ? 'alertTimestamp' : this.sortBy,
  sortDir: this.selectedSortDirection,
  page: this.currentPage - 1,     // ✅ 0-based index
  size: this.pageSize             // ✅ number of results per page
}).subscribe(res => {
  const data = res.content;

  this.data = data.map(d => ({
    location: d.location,
    time: d.timestamp,
    density: d.trafficDensity,
    speed: d.avgSpeed,
    congestion: d.congestionLevel,
    alert: d.alertTimestamp
  }));

  this.filteredData = [...this.data]; // now only contains one page

  this.totalPages = Math.ceil(res.totalElements / this.pageSize); // ✅ use real total
  this.updatePagination();

  this.updateCharts();
  this.isAutoRefresh = false;
  this.isFirstLoad = false;
});



}



applyFilters(): void {
  const now = new Date();
  const fromDateObj = this.fromDate ? new Date(this.fromDate) : null;
  const toDateObj = this.toDate ? new Date(this.toDate) : null;

  if ((fromDateObj && fromDateObj > now) || (toDateObj && toDateObj > now)) {
    this.toastr.error('Date cannot be in the future.', 'Invalid Date', {
      timeOut: 8000,
      closeButton: true,
      positionClass: 'toast-bottom-right'
    });
    return;
  }
    if (fromDateObj && toDateObj && toDateObj < fromDateObj) {
    this.toastr.error('2nd Date cannot be before 1st Date.', 'Invalid Date Range', {
      timeOut: 8000,
      closeButton: true,
      positionClass: 'toast-bottom-right'
    });
    return;
  }

  this.selectedSortDirection = this.pendingSortDirection;
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




  goToPage(page: number) {
  if (page >= 1 && page <= this.totalPages) {
    this.currentPage = page;
    this.updatePagination();
  }
}

updatePagination(): void {
  this.paginatedData = this.filteredData;  // ← use as-is (already paged by backend)
  this.pages = this.generatePagination(this.currentPage, this.totalPages);
  this.updateCharts();
}



generatePagination(current: number, total: number): number[] {
  const range: number[] = [];

  if (total <= 5) {
    for (let i = 1; i <= total; i++) range.push(i);
  } else {
    const showLeftEllipsis = current > 3;
    const showRightEllipsis = current < total - 2;

    range.push(1);

    if (showLeftEllipsis) {
      range.push(-1); // backward ellipsis
    }

    const start = Math.max(2, current - 1);
    const end = Math.min(total - 1, current + 1);

    for (let i = start; i <= end; i++) {
      range.push(i);
    }

    if (showRightEllipsis) {
      range.push(-2); // forward ellipsis
    }

    range.push(total);
  }

  return range;
}







  nextPage() {
    if ((this.currentPage * this.pageSize) < this.filteredData.length) {
      this.currentPage++;
      this.showVisualizations = false;
      this.updatePagination();
    }
  }

  prevPage() {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.showVisualizations = false;
      this.updatePagination();
    }
  }


handleEllipsisClick(direction: 'forward' | 'backward') {
  if (direction === 'forward') {
    const newPage = Math.min(this.currentPage + 3, this.totalPages);
    this.goToPage(newPage);
  } else if (direction === 'backward') {
    const newPage = Math.max(this.currentPage - 3, 1);
    this.goToPage(newPage);
  }
}






get filterSummary(): string {
  const location = this.selectedLocation || 'All locations';
  const congestion = this.selectedCongestion || 'All congestion levels';

  const sortMap: Record<string, string> = {
    location: 'location',
    time: 'time',
    density: 'traffic density',
    speed: 'speed',
    congestion: 'congestion',
    alert: 'alert'
  };


  const column = sortMap[this.sortBy] || 'no sort';
  const dirLabel = this.pendingSortDirection === 'asc' ? 'ascending' : 'descending'; // ✅ uses pending value

  return `${location} • ${congestion} • Sorted by ${column} (${dirLabel})`;
}



sortByColumn(column: keyof TrafficData) {
  // Reset all other directions
  Object.keys(this.sortDirection).forEach(key => {
    if (key !== column) this.sortDirection[key] = undefined;
  });

  const direction = this.sortDirection[column] === 'asc' ? 'desc' : 'asc';
  this.sortDirection[column] = direction;

  this.sortBy = column; // ✅ Set the current sort column for summary

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
updateCharts(): void {
  const sorted = [...this.paginatedData].sort(
    (a, b) => new Date(a.time).getTime() - new Date(b.time).getTime()
  );

  const chartLabels = sorted.map(d => new Date(d.time).toLocaleTimeString());
  const avgSpeeds = sorted.map(d => d.speed);
  const trafficDensities = sorted.map(d => d.density);

  // ✅ Update the chart data using only paginated data
  this.speedChartData.labels = chartLabels;
  this.speedChartData.datasets[0].data = avgSpeeds;

  this.densityChartData.labels = chartLabels;
  this.densityChartData.datasets[0].data = trafficDensities;

  const last = avgSpeeds[avgSpeeds.length - 1] ?? 0;
  const prev = avgSpeeds[avgSpeeds.length - 2] ?? last;

  this.speedTrend = last > prev ? 'up' : last < prev ? 'down' : 'flat';
  this.speedChange = prev === 0 ? 0 : ((last - prev) / prev) * 100;
}




}
