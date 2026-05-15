import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { IconField } from 'primeng/iconfield';
import { InputIcon } from 'primeng/inputicon';
import { InputText } from 'primeng/inputtext';
import { Button } from 'primeng/button';
import { Checkbox } from 'primeng/checkbox';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { LoginData } from '../../models/loginData.model';


@Component({
  selector: 'app-login',
  imports: [Button, IconField, InputIcon, InputText, Checkbox, RouterLink, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  private authService = inject(AuthService);
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  errorMessage: string = '';

  loginForm: FormGroup = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]],
  })

  onSubmit(){
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.errorMessage = '';
    console.log(this.loginForm.getRawValue());
    this.authService.login(this.loginForm.getRawValue()).subscribe({
      next: (user) => {
        console.log('Login successful', user);
        this.router.navigate(['/'])
      },
      error: (err) => {
        console.error('Login failed', err);
        this.errorMessage = 'Neispravan email ili lozinka.';
        this.cdr.detectChanges();
      }
    })
  }
}
