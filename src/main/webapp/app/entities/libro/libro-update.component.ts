import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { debounceTime, distinctUntilChanged, map } from 'rxjs/operators';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { ILibro } from 'app/shared/model/libro.model';
import { IAutor } from 'app/shared/model/autor.model';
import { LibroService } from './libro.service';
import { AutorService } from '../autor/autor.service';

@Component({
    selector: 'jhi-libro-update',
    templateUrl: './libro-update.component.html'
})
export class LibroUpdateComponent implements OnInit {
    libro: ILibro;
    isSaving: boolean;
    public model: any;
    autores: IAutor[];

    constructor(
        private libroService: LibroService,
        private activatedRoute: ActivatedRoute,
        private autorService: AutorService,
        private jhiAlertService: JhiAlertService
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ libro }) => {
            this.libro = libro;
        });

        this.autorService.query().subscribe(
            (res: HttpResponse<IAutor[]>) => {
                this.autores = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    private onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
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

    search = (text$: Observable<string>) =>
        text$.pipe(
            debounceTime(200),
            distinctUntilChanged(),
            map(
                term =>
                    term.length < 2
                        ? []
                        : this.autores
                              .map(a => a.nombre)
                              .filter(v => v.toLowerCase().indexOf(term.toLowerCase()) > -1)
                              .slice(0, 10)
            )
        );
}
