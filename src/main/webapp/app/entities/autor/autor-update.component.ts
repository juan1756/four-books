import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';

import { IAutor } from 'app/shared/model/autor.model';
import { AutorService } from './autor.service';

@Component({
    selector: 'jhi-autor-update',
    templateUrl: './autor-update.component.html'
})
export class AutorUpdateComponent implements OnInit {
    autor: IAutor;
    isSaving: boolean;
    birthDateDp: any;

    constructor(private autorService: AutorService, private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ autor }) => {
            this.autor = autor;
        });
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
}
