import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { FourbooksSharedModule } from 'app/shared';
import {
    LibroComponent,
    LibroDetailComponent,
    LibroUpdateComponent,
    LibroDeletePopupComponent,
    LibroDeleteDialogComponent,
    libroRoute,
    libroPopupRoute
} from './';

const ENTITY_STATES = [...libroRoute, ...libroPopupRoute];

@NgModule({
    imports: [FourbooksSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [LibroComponent, LibroDetailComponent, LibroUpdateComponent, LibroDeleteDialogComponent, LibroDeletePopupComponent],
    entryComponents: [LibroComponent, LibroUpdateComponent, LibroDeleteDialogComponent, LibroDeletePopupComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class FourbooksLibroModule {}
