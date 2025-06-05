import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-section-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './section-card.component.html',
  styleUrls: ['./section-card.component.scss']
})
export class SectionCardComponent implements OnInit {
  @Input() title = '';
  expanded = true;

  emoji = '';
  cleanTitle = '';

  ngOnInit() {
    const match = this.title.match(/^[^\w\s]+/); // Emoji or symbol
    this.emoji = match ? match[0] : '';
    this.cleanTitle = this.title.replace(/^[^\w\s]+/, '').trim();
  }

  toggleExpanded() {
    this.expanded = !this.expanded;
  }
}
