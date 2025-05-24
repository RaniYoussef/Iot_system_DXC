import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from '../header/header.component'; // ✅ Import your standalone component
import { AlertComponent } from '../alert/alert.component';



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
    HeaderComponent,
    CommonModule, // ✅ for *ngFor, *ngIf, ngClass
    FormsModule,   // ✅ for [(ngModel)]
    AlertComponent
  ],
  templateUrl: './traffic-dashboard.component.html',
  styleUrls: ['./traffic-dashboard.component.scss']
})

export class TrafficDashboardComponent implements OnInit {
  // data: TrafficData[] = [
  //   { location: 'Central Avenue', time: '5/14/2025, 10:15:07 PM', density: 462, speed: 12, congestion: 'High' },
  //   { location: 'Downtown', time: '5/14/2025, 10:15:07 PM', density: 894, speed: 30, congestion: 'High' },
  //   { location: 'Broadway', time: '5/14/2025, 10:15:07 PM', density: 202, speed: 32, congestion: 'Low' },
  //   // Add more mock rows here
  // ];

  data: TrafficData[] = [
  {
    location: 'Central Avenue',
    time: '5/14/2025, 10:15:07 PM',
    density: 462,
    speed: 12,
    congestion: 'High',
    alert: '5/14/2025, 10:20:00 PM'
  },
  {
    location: 'Downtown',
    time: '5/14/2025, 10:15:07 PM',
    density: 894,
    speed: 30,
    congestion: 'High'
  },
  {
    location: 'Broadway',
    time: '5/14/2025, 10:15:07 PM',
    density: 202,
    speed: 32,
    congestion: 'Low',
    alert: '5/14/2025, 10:25:00 PM'
  },
  // Add more entries
  { location: 'Sunset Blvd', time: '5/14/2025, 10:30:00 PM', density: 300, speed: 28, congestion: 'Moderate' },
  { location: 'Main Street', time: '5/14/2025, 10:35:00 PM', density: 450, speed: 15, congestion: 'High', alert: '5/14/2025, 10:40:00 PM' },
  { location: 'Market Street', time: '5/14/2025, 10:45:00 PM', density: 180, speed: 35, congestion: 'Low' },
  { location: 'Elm Street', time: '5/14/2025, 10:50:00 PM', density: 520, speed: 18, congestion: 'High' },
  { location: 'Pine Avenue', time: '5/14/2025, 11:00:00 PM', density: 320, speed: 20, congestion: 'Moderate' },
  { location: 'Maple Road', time: '5/14/2025, 11:10:00 PM', density: 410, speed: 25, congestion: 'Moderate' },
  { location: 'Oak Lane', time: '5/14/2025, 11:20:00 PM', density: 360, speed: 22, congestion: 'Low' },
  { location: 'Cedar Drive', time: '5/14/2025, 11:30:00 PM', density: 600, speed: 10, congestion: 'High', alert: '5/14/2025, 11:35:00 PM' }
];

  
  filteredData: TrafficData[] = [...this.data];
  paginatedData: TrafficData[] = [];

  locations = ['Central Avenue', 'Downtown', 'Broadway'];
  selectedLocation = '';
  selectedCongestion = '';
  fromDate = '';
  toDate = '';
  sortBy = ''; // No Sort by default

  currentPage = 1;
  pageSize = 10;

  sortDirection: { [key: string]: 'asc' | 'desc' | undefined } = {};

  totalPages = 1;
  pages: number[] = [];

  selectedSortDirection: 'asc' | 'desc' = 'desc'; // ✅ currently applied
  pendingSortDirection: 'asc' | 'desc' = 'desc';   // ✅ bound to dropdown


  ngOnInit() {
      for (let i = 1; i <= 50; i++) {
    this.data.push({
      location: `Street ${i}`,
      time: `5/14/2025, 10:${(i % 60).toString().padStart(2, '0')}:00 PM`,
      density: Math.floor(Math.random() * 900) + 100,
      speed: Math.floor(Math.random() * 50) + 10,
      congestion: ['Low', 'Moderate', 'High'][i % 3],
      alert: i % 4 === 0 ? `5/14/2025, 10:${(i % 60).toString().padStart(2, '0')}:30 PM` : undefined
    });
  }

    this.filteredData = [...this.data]; // Add this line if not already
    this.updatePagination();
  }

applyFilters() {
  this.selectedSortDirection = this.pendingSortDirection; // ✅ apply new sort direction

  this.filteredData = this.data.filter(item => {
    const matchLocation = this.selectedLocation ? item.location === this.selectedLocation : true;
    const matchCongestion = this.selectedCongestion ? item.congestion === this.selectedCongestion : true;
    return matchLocation && matchCongestion;
  });

  this.sortData(); // ✅ now uses selectedSortDirection
  this.currentPage = 1;
  this.updatePagination();
}


resetFilters() {
  this.selectedLocation = '';
  this.selectedCongestion = '';
  this.fromDate = '';
  this.toDate = '';
  this.sortBy = ''; // No Sort
  this.selectedSortDirection = 'desc';
  this.pendingSortDirection = 'desc';

  this.filteredData = [...this.data];
  this.currentPage = 1;
  this.updatePagination();
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

  this.updatePagination();
}




  goToPage(page: number) {
  if (page >= 1 && page <= this.totalPages) {
    this.currentPage = page;
    this.updatePagination();
  }
}

  updatePagination() {
    const start = (this.currentPage - 1) * this.pageSize;
    const end = start + this.pageSize;
    this.paginatedData = this.filteredData.slice(start, end);

    this.totalPages = Math.ceil(this.filteredData.length / this.pageSize);
    this.pages = this.generatePagination(this.currentPage, this.totalPages);
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
      this.updatePagination();
    }
  }

  prevPage() {
    if (this.currentPage > 1) {
      this.currentPage--;
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

  const sortMap: { [key: string]: string } = {
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




}
