import { NgModule } from '@angular/core';

import { FourbooksSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent } from './';

@NgModule({
    imports: [FourbooksSharedLibsModule],
    declarations: [JhiAlertComponent, JhiAlertErrorComponent],
    exports: [FourbooksSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent]
})
export class FourbooksSharedCommonModule {}
