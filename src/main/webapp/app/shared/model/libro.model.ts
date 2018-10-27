export interface ILibro {
    id?: string;
    isbn?: string;
    titulo?: string;
    editorial?: string;
    edicion?: string;
    activo?: boolean;
}

export class Libro implements ILibro {
    constructor(
        public id?: string,
        public isbn?: string,
        public titulo?: string,
        public editorial?: string,
        public edicion?: string,
        public activo?: boolean
    ) {
        this.activo = this.activo || false;
    }
}
