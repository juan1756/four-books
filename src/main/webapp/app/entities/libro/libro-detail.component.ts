import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ILibro } from 'app/shared/model/libro.model';

@Component({
    selector: 'jhi-libro-detail',
    templateUrl: './libro-detail.component.html'
})
export class LibroDetailComponent implements OnInit {
    libro: ILibro;

    constructor(private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ libro }) => {
            this.libro = libro;
        });
    }

    previousState() {
        window.history.back();
    }
}
