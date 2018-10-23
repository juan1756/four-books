import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ILibro } from 'app/shared/model/libro.model';
import { LibroService } from './libro.service';

@Component({
    selector: 'jhi-libro-update',
    templateUrl: './libro-update.component.html'
})
export class LibroUpdateComponent implements OnInit {
    libro: ILibro;
    isSaving: boolean;

    constructor(private libroService: LibroService, private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ libro }) => {
            this.libro = libro;
        });
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.libro.id !== undefined) {
            this.subscribeToSaveResponse(this.libroService.update(this.libro));
        } else {
            this.subscribeToSaveResponse(this.libroService.create(this.libro));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<ILibro>>) {
        result.subscribe((res: HttpResponse<ILibro>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    private onSaveError() {
        this.isSaving = false;
    }
}
