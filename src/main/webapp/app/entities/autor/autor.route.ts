import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { of } from 'rxjs';
import { map } from 'rxjs/operators';
import { Autor } from 'app/shared/model/autor.model';
import { AutorService } from './autor.service';
import { AutorComponent } from './autor.component';
import { AutorDetailComponent } from './autor-detail.component';
import { AutorUpdateComponent } from './autor-update.component';
import { AutorDeletePopupComponent } from './autor-delete-dialog.component';
import { IAutor } from 'app/shared/model/autor.model';

@Injectable({ providedIn: 'root' })
export class AutorResolve implements Resolve<IAutor> {
    constructor(private service: AutorService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(map((autor: HttpResponse<Autor>) => autor.body));
        }
        return of(new Autor());
    }
}

export const autorRoute: Routes = [
    {
        path: 'autor',
        component: AutorComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Autors'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'autor/:id/view',
        component: AutorDetailComponent,
        resolve: {
            autor: AutorResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Autors'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'autor/new',
        component: AutorUpdateComponent,
        resolve: {
            autor: AutorResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Autors'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'autor/:id/edit',
        component: AutorUpdateComponent,
        resolve: {
            autor: AutorResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Autors'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const autorPopupRoute: Routes = [
    {
        path: 'autor/:id/delete',
        component: AutorDeletePopupComponent,
        resolve: {
            autor: AutorResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Autors'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
