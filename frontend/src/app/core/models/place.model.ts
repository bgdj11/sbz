export interface Place {
  id: number;
  name: string;
  country: string;
  city: string;
  tags?: string[];
  hashtags?: string[];
  description?: string;
  averageRating?: number;
}
