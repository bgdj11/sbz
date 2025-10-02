import { User } from './user.model';

export interface Post {
  id: number;
  content: string;
  createdAt: string;
  author: User;
  likedByUsers?: User[];
  tags?: string[];
  hashtags?: string[];
  likesCount?: number;
  liked?: boolean;
  liking?: boolean;
}
