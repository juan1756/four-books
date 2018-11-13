import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';
import { Observable } from 'rxjs';

import { ILibro } from 'app/shared/model/libro.model';
import { Principal } from 'app/core';
import { LibroService } from './libro.service';

@Component({
    selector: 'jhi-libro',
    templateUrl: './libro.component.html'
})
export class LibroComponent implements OnInit, OnDestroy {
    libros: ILibro[];
    currentAccount: any;
    eventSubscriber: Subscription;
    currentSearch: string;

    constructor(
        private libroService: LibroService,
        private jhiAlertService: JhiAlertService,
        private eventManager: JhiEventManager,
        private activatedRoute: ActivatedRoute,
        private principal: Principal
    ) {
        this.currentSearch =
            this.activatedRoute.snapshot && this.activatedRoute.snapshot.params['search']
                ? this.activatedRoute.snapshot.params['search']
                : '';
    }

    loadAll() {
        if (this.currentSearch) {
            this.libroService
                .search({
                    query: this.currentSearch
                })
                .subscribe(
                    (res: HttpResponse<ILibro[]>) => (this.libros = res.body),
                    (res: HttpErrorResponse) => this.onError(res.message)
                );
            return;
        }
        this.libroService.query().subscribe(
            (res: HttpResponse<ILibro[]>) => {
                this.libros = res.body;
                this.currentSearch = '';
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    search(query) {
        if (!query) {
            return this.clear();
        }
        this.currentSearch = query;
        this.loadAll();
    }

    clear() {
        this.currentSearch = '';
        this.loadAll();
    }

    ngOnInit() {
        this.loadAll();
        this.principal.identity().then(account => {
            this.currentAccount = account;
        });
        this.registerChangeInLibros();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: ILibro) {
        return item.id;
    }

    registerChangeInLibros() {
        this.eventSubscriber = this.eventManager.subscribe('libroListModification', response => this.loadAll());
    }

    private onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }

    recommend(idLibro: string) {
        this.subscribeToSaveResponse(this.libroService.recommend(this.libros.find(x => x.id === idLibro)));
    }

    unRecommend(idLibro: string) {
        this.subscribeToSaveResponse(this.libroService.unRecommend(idLibro));
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<ILibro>>) {
        result.subscribe((res: HttpResponse<ILibro>) => this.onRecommendSuccess(), this.onRecommendError);
    }

    onRecommendSuccess() {
        this.loadAll();
    }

    onRecommendError(res: HttpErrorResponse) {
        console.log(res);
    }
}
