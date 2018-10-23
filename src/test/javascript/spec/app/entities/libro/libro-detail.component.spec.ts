/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { FourbooksTestModule } from '../../../test.module';
import { LibroDetailComponent } from 'app/entities/libro/libro-detail.component';
import { Libro } from 'app/shared/model/libro.model';

describe('Component Tests', () => {
    describe('Libro Management Detail Component', () => {
        let comp: LibroDetailComponent;
        let fixture: ComponentFixture<LibroDetailComponent>;
        const route = ({ data: of({ libro: new Libro('123') }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [FourbooksTestModule],
                declarations: [LibroDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(LibroDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(LibroDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.libro).toEqual(jasmine.objectContaining({ id: '123' }));
            });
        });
    });
});
