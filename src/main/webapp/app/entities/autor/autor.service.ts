import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IAutor } from 'app/shared/model/autor.model';

type EntityResponseType = HttpResponse<IAutor>;
type EntityArrayResponseType = HttpResponse<IAutor[]>;

@Injectable({ providedIn: 'root' })
export class AutorService {
    private resourceUrl = SERVER_API_URL + 'api/autors';
    private resourceSearchUrl = SERVER_API_URL + 'api/_search/autors';

    constructor(private http: HttpClient) {}

    create(autor: IAutor): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(autor);
        return this.http
            .post<IAutor>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    update(autor: IAutor): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(autor);
        return this.http
            .put<IAutor>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    find(id: string): Observable<EntityResponseType> {
        return this.http
            .get<IAutor>(`${this.resourceUrl}/${id}`, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<IAutor[]>(this.resourceUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    delete(id: string): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<IAutor[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    private convertDateFromClient(autor: IAutor): IAutor {
        const copy: IAutor = Object.assign({}, autor, {
            birthDate: autor.birthDate != null && autor.birthDate.isValid() ? autor.birthDate.format(DATE_FORMAT) : null
        });
        return copy;
    }

    private convertDateFromServer(res: EntityResponseType): EntityResponseType {
        res.body.birthDate = res.body.birthDate != null ? moment(res.body.birthDate) : null;
        return res;
    }

    private convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
        res.body.forEach((autor: IAutor) => {
            autor.birthDate = autor.birthDate != null ? moment(autor.birthDate) : null;
        });
        return res;
    }
}
