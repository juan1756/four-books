import { ILibro } from 'app/shared/model//libro.model';

export interface IAutor {
    id?: string;
    nombre?: string;
    nacionalidad?: string;
    email?: string;
    libros?: ILibro;
}

export class Autor implements IAutor {
    constructor(public id?: string, public nombre?: string, public nacionalidad?: string, public email?: string, public libros?: ILibro) {}
}
