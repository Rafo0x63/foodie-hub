import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { IconField } from 'primeng/iconfield';
import { InputIcon } from 'primeng/inputicon';
import { InputText } from 'primeng/inputtext';
import { Button } from 'primeng/button';
import { Checkbox } from 'primeng/checkbox';
import { AuthService } from '../../core/services/auth.service';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { RegisterData } from '../../models/registerData.model';
import { CommonModule } from '@angular/common';

function passwordsMatchValidator(group: AbstractControl): ValidationErrors | null {
  const password = group.get('password')?.value;
  const confirm = group.get('confirmPassword')?.value;
  return password === confirm ? null : { passwordsMismatch: true };
}

@Component({
  selector: 'app-register',
  imports: [Button, IconField, InputIcon, InputText, Checkbox, RouterLink, ReactiveFormsModule, CommonModule],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  private authService = inject(AuthService);
  private fb = inject(FormBuilder);
  private router = inject(Router);

  errorMessage = '';

  registerForm: FormGroup = this.fb.nonNullable.group(
    {
      name: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required]],
      agreeToTerms: [false, [Validators.requiredTrue]],
    },
    { validators: passwordsMatchValidator }
  );

  onSubmit() {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    const { name, email, password } = this.registerForm.getRawValue();
    const registerData: RegisterData = { name, email, password };

    this.authService.register(registerData).subscribe({
      next: () => {
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.errorMessage = err.error?.message ?? 'Registracija nije uspjela. Pokušajte ponovo.';
      },
    });
  }
}
