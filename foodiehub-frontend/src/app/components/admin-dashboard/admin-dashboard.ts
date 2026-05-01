import {ChangeDetectorRef, Component, inject} from '@angular/core';
import {UserDTO} from '../../models/user.model';
import {AdminService} from '../../core/services/admin.service';
import {TableModule} from 'primeng/table';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [ButtonModule, TableModule],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.css',
})
export class AdminDashboard {
  private adminService = inject(AdminService);
  private readonly cdr = inject(ChangeDetectorRef);

  users: UserDTO[] = [];
  loading = false;
  activeCount: number | null = null;
  adminCount: number | null = null;

  constructor() {
    this.loadUsers();
  }

  loadUsers() {
    this.loading = true;
    console.log("loading users")
    this.adminService.getUsers().subscribe({
      next: (data) => {
        this.users = data;
        console.log(this.users);
        this.activeCount = this.users.length;
        this.adminCount = this.users.filter(u => u.role != "ROLE_CHEF").length;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => this.loading = false
    });
  }

  deleteUser(user: any) {
    console.log('delete user', user);
  }

  openCreateUser() {
    console.log('open create user dialog');
  }
}
