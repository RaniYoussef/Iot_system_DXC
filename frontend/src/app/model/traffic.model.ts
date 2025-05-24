export interface TrafficReadingWithAlert {
  id: number;
  location: string;
  timestamp: string;
  trafficDensity: number;
  avgSpeed: number;
  congestionLevel: string;
  alertTimestamp?: string | null;
}
