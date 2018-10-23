import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IAutor } from 'app/shared/model/autor.model';

@Component({
    selector: 'jhi-autor-detail',
    templateUrl: './autor-detail.component.html'
})
export class AutorDetailComponent implements OnInit {
    autor: IAutor;

    constructor(private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ autor }) => {
            this.autor = autor;
        });
    }

    previousState() {
        window.history.back();
    }
}
