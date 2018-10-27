import { Moment } from 'moment';

export interface IAutor {
    id?: string;
    nombre?: string;
    nacionalidad?: string;
    email?: string;
    birthDate?: Moment;
}

export class Autor implements IAutor {
    constructor(
        public id?: string,
        public nombre?: string,
        public nacionalidad?: string,
        public email?: string,
        public birthDate?: Moment
    ) {}
}
