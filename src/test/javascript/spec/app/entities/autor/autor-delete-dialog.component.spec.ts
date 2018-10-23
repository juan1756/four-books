/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { FourbooksTestModule } from '../../../test.module';
import { AutorDeleteDialogComponent } from 'app/entities/autor/autor-delete-dialog.component';
import { AutorService } from 'app/entities/autor/autor.service';

describe('Component Tests', () => {
    describe('Autor Management Delete Component', () => {
        let comp: AutorDeleteDialogComponent;
        let fixture: ComponentFixture<AutorDeleteDialogComponent>;
        let service: AutorService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [FourbooksTestModule],
                declarations: [AutorDeleteDialogComponent]
            })
                .overrideTemplate(AutorDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(AutorDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(AutorService);
            mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
            mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
        });

        describe('confirmDelete', () => {
            it(
                'Should call delete service on confirmDelete',
                inject(
                    [],
                    fakeAsync(() => {
                        // GIVEN
                        spyOn(service, 'delete').and.returnValue(of({}));

                        // WHEN
                        comp.confirmDelete('123');
                        tick();

                        // THEN
                        expect(service.delete).toHaveBeenCalledWith('123');
                        expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                        expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
                    })
                )
            );
        });
    });
});
