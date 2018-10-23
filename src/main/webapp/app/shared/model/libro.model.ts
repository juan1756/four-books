import { IAutor } from 'app/shared/model//autor.model';

export interface ILibro {
    id?: string;
    isbn?: string;
    titulo?: string;
    descripcion?: string;
    editorial?: string;
    edicion?: string;
    autors?: IAutor[];
}

export class Libro implements ILibro {
    constructor(
        public id?: string,
        public isbn?: string,
        public titulo?: string,
        public descripcion?: string,
        public editorial?: string,
        public edicion?: string,
        public autors?: IAutor[]
    ) {}
}
