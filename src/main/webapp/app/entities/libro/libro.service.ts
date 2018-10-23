import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { ILibro } from 'app/shared/model/libro.model';

type EntityResponseType = HttpResponse<ILibro>;
type EntityArrayResponseType = HttpResponse<ILibro[]>;

@Injectable({ providedIn: 'root' })
export class LibroService {
    private resourceUrl = SERVER_API_URL + 'api/libros';
    private resourceSearchUrl = SERVER_API_URL + 'api/_search/libros';

    constructor(private http: HttpClient) {}

    create(libro: ILibro): Observable<EntityResponseType> {
        return this.http.post<ILibro>(this.resourceUrl, libro, { observe: 'response' });
    }

    update(libro: ILibro): Observable<EntityResponseType> {
        return this.http.put<ILibro>(this.resourceUrl, libro, { observe: 'response' });
    }

    find(id: string): Observable<EntityResponseType> {
        return this.http.get<ILibro>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<ILibro[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: string): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<ILibro[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
    }
}
