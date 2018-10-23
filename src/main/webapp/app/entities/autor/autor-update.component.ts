import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { JhiAlertService } from 'ng-jhipster';

import { IAutor } from 'app/shared/model/autor.model';
import { AutorService } from './autor.service';
import { ILibro } from 'app/shared/model/libro.model';
import { LibroService } from 'app/entities/libro';

@Component({
    selector: 'jhi-autor-update',
    templateUrl: './autor-update.component.html'
})
export class AutorUpdateComponent implements OnInit {
    autor: IAutor;
    isSaving: boolean;

    libros: ILibro[];

    constructor(
        private jhiAlertService: JhiAlertService,
        private autorService: AutorService,
        private libroService: LibroService,
        private activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ autor }) => {
            this.autor = autor;
        });
        this.libroService.query().subscribe(
            (res: HttpResponse<ILibro[]>) => {
                this.libros = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.autor.id !== undefined) {
            this.subscribeToSaveResponse(this.autorService.update(this.autor));
        } else {
            this.subscribeToSaveResponse(this.autorService.create(this.autor));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<IAutor>>) {
        result.subscribe((res: HttpResponse<IAutor>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    private onSaveError() {
        this.isSaving = false;
    }

    private onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }

    trackLibroById(index: number, item: ILibro) {
        return item.id;
    }
}
