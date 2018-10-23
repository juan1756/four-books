import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { of } from 'rxjs';
import { map } from 'rxjs/operators';
import { Libro } from 'app/shared/model/libro.model';
import { LibroService } from './libro.service';
import { LibroComponent } from './libro.component';
import { LibroDetailComponent } from './libro-detail.component';
import { LibroUpdateComponent } from './libro-update.component';
import { LibroDeletePopupComponent } from './libro-delete-dialog.component';
import { ILibro } from 'app/shared/model/libro.model';

@Injectable({ providedIn: 'root' })
export class LibroResolve implements Resolve<ILibro> {
    constructor(private service: LibroService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(map((libro: HttpResponse<Libro>) => libro.body));
        }
        return of(new Libro());
    }
}

export const libroRoute: Routes = [
    {
        path: 'libro',
        component: LibroComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Libros'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'libro/:id/view',
        component: LibroDetailComponent,
        resolve: {
            libro: LibroResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Libros'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'libro/new',
        component: LibroUpdateComponent,
        resolve: {
            libro: LibroResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Libros'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'libro/:id/edit',
        component: LibroUpdateComponent,
        resolve: {
            libro: LibroResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Libros'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const libroPopupRoute: Routes = [
    {
        path: 'libro/:id/delete',
        component: LibroDeletePopupComponent,
        resolve: {
            libro: LibroResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Libros'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
