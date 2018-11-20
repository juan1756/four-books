export interface ILibro {
    id?: string;
    isbn?: string;
    titulo?: string;
    editorial?: string;
    edicion?: string;
    activo?: boolean;
    autor?: string;
}

export class Libro implements ILibro {
    constructor(
        public id?: string,
        public isbn?: string,
        public titulo?: string,
        public editorial?: string,
        public edicion?: string,
        public activo?: boolean,
        public autor?: string
    ) {
        this.activo = this.activo || false;
    }
}
