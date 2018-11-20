import { Component, OnInit } from '@angular/core';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { LoginModalService, Principal, Account } from 'app/core';
import { LibroService } from '../entities/libro/libro.service';
import { ILibro } from 'app/shared/model/libro.model';

@Component({
    selector: 'jhi-home',
    templateUrl: './home.component.html',
    styleUrls: ['home.scss']
})
export class HomeComponent implements OnInit {
    account: Account;
    modalRef: NgbModalRef;
    libros: ILibro[];

    constructor(
        private principal: Principal,
        private loginModalService: LoginModalService,
        private eventManager: JhiEventManager,
        private libroService: LibroService,
        private jhiAlertService: JhiAlertService
    ) {}

    ngOnInit() {
        this.principal.identity().then(account => {
            this.account = account;
        });
        this.registerAuthenticationSuccess();
        this.loadAll();
    }

    registerAuthenticationSuccess() {
        this.eventManager.subscribe('authenticationSuccess', message => {
            this.principal.identity().then(account => {
                this.account = account;
            });
        });
    }

    isAuthenticated() {
        return this.principal.isAuthenticated();
    }

    login() {
        this.modalRef = this.loginModalService.open();
    }

    loadAll() {
        this.libroService.getRecomendados().subscribe(
            (res: HttpResponse<ILibro[]>) => {
                this.libros = res.body;
                console.log('Libros cargados!', this.libros);
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    private onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }
}
