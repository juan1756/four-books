/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { FourbooksTestModule } from '../../../test.module';
import { LibroUpdateComponent } from 'app/entities/libro/libro-update.component';
import { LibroService } from 'app/entities/libro/libro.service';
import { Libro } from 'app/shared/model/libro.model';

describe('Component Tests', () => {
    describe('Libro Management Update Component', () => {
        let comp: LibroUpdateComponent;
        let fixture: ComponentFixture<LibroUpdateComponent>;
        let service: LibroService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [FourbooksTestModule],
                declarations: [LibroUpdateComponent]
            })
                .overrideTemplate(LibroUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(LibroUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(LibroService);
        });

        describe('save', () => {
            it(
                'Should call update service on save for existing entity',
                fakeAsync(() => {
                    // GIVEN
                    const entity = new Libro('123');
                    spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.libro = entity;
                    // WHEN
                    comp.save();
                    tick(); // simulate async

                    // THEN
                    expect(service.update).toHaveBeenCalledWith(entity);
                    expect(comp.isSaving).toEqual(false);
                })
            );

            it(
                'Should call create service on save for new entity',
                fakeAsync(() => {
                    // GIVEN
                    const entity = new Libro();
                    spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.libro = entity;
                    // WHEN
                    comp.save();
                    tick(); // simulate async

                    // THEN
                    expect(service.create).toHaveBeenCalledWith(entity);
                    expect(comp.isSaving).toEqual(false);
                })
            );
        });
    });
});
