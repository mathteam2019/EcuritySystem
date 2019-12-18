<style lang="scss">
  .device-table {
    .form-group {
      label.input-label {
        line-height: 36px;
      }
    }

    div.img-wrapper {
      width: 270px;
      height: 420px;
      padding: 30px;
      border: solid 1px #bdbaba;
      border-radius: 3px;
      position: relative;

      img {
        width: 100%;
        object-fit: scale-down;
      }
    }
  }

</style>
<template>
  <div class="device-table">
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb/>
        </b-colxx>
      </b-row>
    </div>
    <b-card class="main-without-tab">
      <div v-if="pageStatus==='list'" class="h-100 d-flex flex-column">
        <b-row class="pt-2">
          <b-col cols="7">
            <b-row>
              <b-col>
                <b-form-group :label="$t('device-management.device')">
                  <b-form-input v-model="filterOption.deviceName"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group :label="$t('device-management.active')">
                  <b-form-select v-model="filterOption.status" :options="stateOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group :label="$t('device-management.filename')">
                  <b-form-input v-model="filterOption.archivesName"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group :label="$t('device-management.device-classify')">
                  <b-form-select v-model="filterOption.categoryId" :options="categoryFilterData" plain/>
                </b-form-group>
              </b-col>
            </b-row>
          </b-col>
          <b-col cols="5" class="d-flex justify-content-end align-items-center">
            <b-button size="sm" class="ml-2" variant="info default" @click="onSearchButton()">
              <i class="icofont-search-1"></i>&nbsp;{{ $t('permission-management.search') }}
            </b-button>
            <b-button size="sm" class="ml-2" variant="info default" @click="onResetButton()">
              <i class="icofont-ui-reply"></i>&nbsp;{{$t('permission-management.reset') }}
            </b-button>
            <b-button size="sm" class="ml-2" variant="outline-info default" @click="onExportButton()" :disabled="checkPermItem('device_export')">
              <i class="icofont-share-alt"></i>&nbsp;{{ $t('permission-management.export') }}
            </b-button>
            <b-button size="sm" class="ml-2" variant="outline-info default" @click="onPrintButton" :disabled="checkPermItem('device_print')">
              <i class="icofont-printer"></i>&nbsp;{{ $t('permission-management.print') }}
            </b-button>
            <b-button size="sm" class="ml-2" @click="onAction('create')" variant="success default" :disabled="checkPermItem('device_create')">
              <i class="icofont-plus"></i>&nbsp;{{$t('permission-management.new') }}
            </b-button>
          </b-col>
        </b-row>

        <b-row class="flex-grow-1">
          <b-col cols="12">
            <div class="table-wrapper table-responsive">
              <vuetable
                ref="vuetable"
                :fields="vuetableItems.fields"
                :api-url="vuetableItems.apiUrl"
                :http-fetch="vuetableHttpFetch"
                :per-page="vuetableItems.perPage"
                pagination-path="pagination"
                track-by="deviceId"
                @vuetable:pagination-data="onPaginationData"
                class="table-striped"
              >
                <div slot="number" slot-scope="props">
                  <span class="cursor-p text-primary" @click="onAction('show',props.rowData)">{{ props.rowData.deviceSerial }}</span>
                </div>
                <div slot="operating" slot-scope="props">
                  <b-button :disabled="props.rowData.status === '1000000701' || checkPermItem('device_modify')" @click="onAction('edit',props.rowData)"
                            size="sm"
                            variant="primary default btn-square"
                  >
                    <i class="icofont-edit"></i>
                  </b-button>
                  <b-button
                    v-if="props.rowData.status=='1000000702'"
                    size="sm" @click="onAction('activate',props.rowData)"
                    variant="success default btn-square" :disabled="checkPermItem('device_update_status')"
                  >
                    <i class="icofont-check-circled"></i>
                  </b-button>
                  <b-button @click="onAction('inactivate',props.rowData)"
                            v-if="props.rowData.status=='1000000701'"
                            size="sm"
                            variant="warning default btn-square" :disabled="checkPermItem('device_update_status')"
                  >
                    <i class="icofont-ban"></i>
                  </b-button>
                  <b-button @click="onAction('delete',props.rowData)"
                            size="sm" :disabled="props.rowData.status === '1000000701' || checkPermItem('device_delete')"
                            variant="danger default btn-square"
                  >
                    <i class="icofont-bin"></i>
                  </b-button>
                </div>
              </vuetable>
            </div>
            <div class="pagination-wrapper">
              <vuetable-pagination-bootstrap
                ref="pagination"
                @vuetable-pagination:change-page="onChangePage"
                @onUpdatePerPage="vuetableItems.perPage = Number($event)"
              ></vuetable-pagination-bootstrap>
            </div>
          </b-col>
        </b-row>
      </div>
      <div v-if="pageStatus==='create'" class="form-section">
        <b-row class="h-100">
          <b-col cols="8">
            <b-row>
              <b-col cols="4">
                <b-form-group>
                  <template slot="label">{{$t('device-management.device-no')}}<span class="text-danger">*</span>
                  </template>
                  <b-form-input v-model="mainForm.deviceSerial" :state="!$v.mainForm.deviceSerial.$dirty ? null : !$v.mainForm.deviceSerial.$invalid"></b-form-input>
                  <div class="invalid-feedback d-block">
                    {{ (submitted && !$v.mainForm.deviceSerial.required) ?
                    $t('device-management.device-classify-item.field-is-mandatory') :"&nbsp;"}}
                  </div>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group>
                  <template slot="label">{{$t('device-management.device')}}<span class="text-danger">*</span>
                  </template>
                  <b-form-input v-model="mainForm.deviceName" :state="!$v.mainForm.deviceName.$dirty ? null : !$v.mainForm.deviceName.$invalid"></b-form-input>
                  <div class="invalid-feedback d-block">
                    {{ (submitted && !$v.mainForm.deviceName.required) ?
                    $t('device-management.device-classify-item.field-is-mandatory') :"&nbsp;"}}
                  </div>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group>
                  <template slot="label">{{$t('device-management.device-list.archive')}}<span
                    class="text-danger">*</span>
                  </template>
                  <b-form-select v-model="mainForm.archiveId" :state="!$v.mainForm.archiveId.$dirty ? null : !$v.mainForm.archiveId.$invalid" :options="archivesSelectOptions" plain/>
                  <div class="invalid-feedback d-block">
                    {{ (submitted && !$v.mainForm.archiveId.required) ?
                    $t('device-management.device-classify-item.field-is-mandatory') :"&nbsp;"}}
                  </div>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group>
                  <template slot="label">{{$t('device-management.device-classify')}}<span class="text-danger">*</span>
                  </template>
                  <label class="input-label">{{archiveForm.category}}</label>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.manufacture')">
                  <label class="input-label">{{archiveForm.manufacturer}}</label>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.origin-model')">
                  <label class="input-label">{{archiveForm.originalModel}}</label>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row class="mb-5" v-if="mainForm.archiveId>0">
              <b-col cols="12" class="d-flex align-items-center">
                <label class="pr-2 m-0 "
                       style="color: #bdbaba">{{$t('device-management.device-list.device-information')}}</label>
                <div class="flex-grow-1" style="height: 1px;background-color: #bdbaba"></div>
              </b-col>
            </b-row>
            <b-row v-show="mainForm.archiveId>0">
              <b-col cols="4">
                <b-form-group>
                  <template slot="label">{{$t('device-management.device-table.device-type')}}<span
                    class="text-danger">*</span>
                  </template>
                  <b-form-select v-model="mainForm.deviceType" :options="deviceTypeSelectOptions" plain/>
                  <div class="invalid-feedback d-block">
                    {{ (submitted && !$v.mainForm.deviceType.required) ?
                    $t('device-management.device-classify-item.field-is-mandatory') :"&nbsp;"}}
                  </div>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group>
                  <template slot="label">{{$t('device-management.device-table.guid')}}<span
                    class="text-danger">*</span>
                  </template>
                  <b-form-input v-model="mainForm.guid"></b-form-input>
                  <div class="invalid-feedback d-block">
                    {{ (submitted && !$v.mainForm.guid.required) ?
                    $t('device-management.device-classify-item.field-is-mandatory') :"&nbsp;"}}
                  </div>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.device-list.original-number')">
                  <b-form-input v-model="mainForm.originalFactoryNumber"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.device-list.production-date')">
                  <b-form-input type="date" v-model="mainForm.manufacturerDate"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.device-list.purchase-date')">
                  <b-form-input type="date" v-model="mainForm.purchaseDate"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.device-list.supplier')">
                  <b-form-select v-model="mainForm.supplier" :options="manufacturerOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.device-list.supplier-contact')">
                  <b-form-input v-model="mainForm.contacts"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.device-list.supplier-contact-information')">
                  <b-form-input v-model="mainForm.mobile"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.device-list.ip')">
                  <b-form-input v-model="mainForm.deviceIp"></b-form-input>
                </b-form-group>
              </b-col>

            </b-row>
          </b-col>
          <b-col cols="4" class="d-flex flex-column align-items-center">
            <div class="img-wrapper">
              <img v-if="mainForm.image!=null&&mainForm.image!==''" :src="mainForm.image"/>
              <img v-else-if="!(mainForm.image!=null&&mainForm.image!=='')"
                   src="../../../assets/img/device.png">
              <div class="position-absolute" style="bottom: -18%;left: -41%">
                <img v-if="mainForm.status === '1000000701'" src="../../../assets/img/active_stamp.png">
                <img v-else-if="mainForm.status === '1000000702'" src="../../../assets/img/no_active_stamp.png">
              </div>
            </div>
            <input type="file" ref="imgFile" @change="onFileChange" style="display: none"/>
            <b-button @click="$refs.imgFile.click()" class="mt-3" variant="info skyblue default" size="sm">{{
              $t('permission-management.upload-image')}}
            </b-button>
          </b-col>
          <b-col cols="12 d-flex align-items-end justify-content-end mt-3">
            <div>
              <b-button size="sm" @click="saveDeviceItem()" variant="info default" ><i class="icofont-save"></i>
                {{$t('device-management.save')}}
              </b-button>

              <b-button size="sm" variant="info default" @click="onAction('show-list')"><i
                class="icofont-long-arrow-left"></i> {{$t('device-management.return')}}
              </b-button>
            </div>
          </b-col>
        </b-row>
      </div>

      <div v-if="pageStatus==='show' || pageStatus === 'edit'" class="h-100 d-flex flex-grow-1 flex-column pb-3">
        <b-tabs class="sub-tabs" nav-class="separator-tabs ml-0" content-class="tab-content"
                :no-fade="true">
          <b-tab :title="$t('device-management.device-table.device-info')">
            <b-row class="h-100 form-section">
              <b-col cols="8">
                <b-row>
                  <b-col cols="4">
                    <b-form-group>
                      <template slot="label">{{$t('device-management.device-no')}}<span class="text-danger">*</span>
                      </template>
                      <label class="input-label">{{mainForm.deviceSerial}}</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group>
                      <template slot="label">{{$t('device-management.device')}}<span class="text-danger">*</span>
                      </template>
                      <label class="input-label">{{mainForm.deviceName}}</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group>
                      <template slot="label">{{$t('device-management.device-list.archive')}}<span
                        class="text-danger">*</span>
                      </template>
                      <label class="input-label">{{archiveForm.name}}</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group>
                      <template slot="label">{{$t('device-management.device-classify')}}<span
                        class="text-danger">*</span>
                      </template>
                      <label class="input-label">{{archiveForm.category}}</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.manufacture')">
                      <label class="input-label">{{archiveForm.manufacturer}}</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.device-model')">
                      <label class="input-label">{{archiveForm.originalModel}}</label>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row class="mb-5">
                  <b-col cols="12" class="d-flex align-items-center">
                    <label class="pr-2 m-0 "
                           style="color: #bdbaba">{{$t('device-management.device-list.device-information')}}</label>
                    <div class="flex-grow-1" style="height: 1px;background-color: #bdbaba"></div>
                  </b-col>
                </b-row>
                <b-row >
                  <b-col cols="4">
                    <b-form-group>
                      <template slot="label">{{$t('device-management.device-table.device-type')}}<span
                        class="text-danger">*</span>
                      </template>
                      <b-form-select v-model="mainForm.deviceType" :options="deviceTypeSelectOptions" plain/>
                      <div v-if="pageStatus === 'edit'" class="invalid-feedback d-block">
                        {{ (submitted && !$v.mainForm.deviceType.required) ?
                        $t('device-management.device-classify-item.field-is-mandatory') :"&nbsp;"}}
                      </div>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group>
                      <template slot="label">{{$t('device-management.device-table.guid')}}<span
                        class="text-danger">*</span>
                      </template>
                      <b-form-input v-model="mainForm.guid"></b-form-input>
                      <div v-if="pageStatus === 'edit'" class="invalid-feedback d-block">
                        {{ (submitted && !$v.mainForm.guid.required) ?
                        $t('device-management.device-classify-item.field-is-mandatory') :"&nbsp;"}}
                      </div>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.device-list.original-number')">
                      <b-form-input v-model="mainForm.originalFactoryNumber"></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.device-list.production-date')">
                      <b-form-input type="date" v-model="mainForm.manufacturerDate"></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.device-list.purchase-date')">
                      <b-form-input type="date" v-model="mainForm.purchaseDate"></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.device-list.supplier')">
                      <b-form-select v-model="mainForm.supplier" :options="manufacturerOptions" plain/>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.device-list.supplier-contact')">
                      <b-form-input v-model="mainForm.contacts"></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.device-list.supplier-contact-information')">
                      <b-form-input v-model="mainForm.mobile"></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.device-list.ip')">
                      <b-form-input v-model="mainForm.deviceIp"></b-form-input>
                    </b-form-group>
                  </b-col>
                </b-row>
              </b-col>
              <b-col cols="4" class="d-flex flex-column align-items-center">
                <div class="img-wrapper">
                  <img v-if="mainForm.image!=null&&mainForm.image!==''" :src="mainForm.image"/>
                  <img v-else-if="!(mainForm.image!=null&&mainForm.image!=='')"
                       src="../../../assets/img/device.png">
                  <div class="position-absolute" style="bottom: -18%;left: -41%">
                    <img v-if="mainForm.status === '1000000701'" src="../../../assets/img/active_stamp.png">
                    <img v-else-if="mainForm.status === '1000000702'" src="../../../assets/img/no_active_stamp.png">
                  </div>
                </div>
                <input type="file" ref="imgFile" @change="onFileChange" style="display: none"/>
                <b-button @click="$refs.imgFile.click()" class="mt-3" variant="info skyblue default" size="sm">{{
                  $t('permission-management.upload-image')}}
                </b-button>
              </b-col>
            </b-row>
          </b-tab>
          <b-tab :title="$t('device-management.device-table.archive-info')" style="height: 200px">
            <b-row class="h-100 form-section">
              <b-col cols="8">
                <b-row>
                  <b-col cols="4">
                    <b-form-group>
                      <template slot="label">{{$t('device-management.file-no')}}<span class="text-danger">*</span>
                      </template>
                      <label class="input-label">{{archiveForm.number}}</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group>
                      <template slot="label">{{$t('device-management.filename')}}<span class="text-danger">*</span>
                      </template>
                      <label class="input-label">{{archiveForm.name}}</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group>
                      <template slot="label">{{$t('device-management.template-name')}}<span class="text-danger">*</span>
                      </template>
                      <label class="input-label">{{archiveForm.templateName}}</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group>
                      <template slot="label">{{$t('device-management.device-classify')}}<span
                        class="text-danger">*</span>
                      </template>
                      <label class="input-label">{{archiveForm.category}}</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.manufacture')">
                      <label class="input-label">{{archiveForm.manufacturer}}</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.origin-model')">
                      <label class="input-label">{{archiveForm.originalModel}}</label>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row class="mb-5">
                  <b-col cols="12" class="d-flex align-items-center">
                    <label class="pr-2 m-0 "
                           style="color: #bdbaba">{{$t('device-management.archive.technical-indicator')}}</label>
                    <div class="flex-grow-1" style="height: 1px;background-color: #bdbaba"></div>
                  </b-col>
                </b-row>
                <b-row >
                  <b-col cols="4" v-for="item in archiveDetailData">
                    <b-form-group :label="item.indicatorsName">
                      <label class="input-label">{{item.value?item.value:''}}</label>
                    </b-form-group>
                  </b-col>

                </b-row>
              </b-col>
              <b-col cols="4" class="d-flex flex-column align-items-center">
                <div class="img-wrapper">
                  <img v-if="mainForm.image!=null&&mainForm.image!==''" :src="mainForm.image"/>
                  <img v-else-if="!(mainForm.image!=null&&mainForm.image!=='')"
                       src="../../../assets/img/device.png">
                  <div class="position-absolute" style="bottom: -18%;left: -41%">
                    <img v-if="mainForm.status === '1000000701'" src="../../../assets/img/active_stamp.png">
                    <img v-else-if="mainForm.status === '1000000702'" src="../../../assets/img/no_active_stamp.png">
                  </div>
                </div>
                <input type="file" ref="imgFile" @change="onFileChange" style="display: none"/>
                <b-button class="mt-3" variant="info skyblue default" size="sm">{{
                  $t('permission-management.upload-image')}}
                </b-button>
              </b-col>
            </b-row>
          </b-tab>
        </b-tabs>
        <div class="d-flex align-items-end justify-content-end flex-grow-1 position-absolute"
             style="right: 30px;bottom: 30px;">
          <div>
            <b-button size="sm" v-if="pageStatus === 'edit'" @click="saveDeviceItem()" variant="info default"><i class="icofont-save"></i>
              {{$t('device-management.save')}}
            </b-button>
            <b-button size="sm" v-if="pageStatus!=='create' && mainForm.status === '1000000702'" :disabled="checkPermItem('device_update_status')"
                      @click="onAction('activate',mainForm)" variant="success default"><i
              class="icofont-check-circled"></i>
              {{$t('device-management.active')}}
            </b-button>
            <b-button size="sm" v-if="pageStatus!=='create' && mainForm.status === 'active'" :disabled="checkPermItem('device_update_status')"
                      @click="onAction('inactivate',mainForm)" variant="warning default"><i class="icofont-ban"></i>
              {{$t('device-management.inactive')}}
            </b-button>
            <b-button size="sm" v-if="pageStatus!=='create' && mainForm.status === 'inactive'" :disabled="checkPermItem('device_delete')"
                      @click="onAction('delete',mainForm)" variant="danger default"><i class="icofont-bin"></i>
              {{$t('device-management.delete')}}
            </b-button>
            <b-button size="sm" variant="info default" @click="onAction('show-list')"><i
              class="icofont-long-arrow-left"></i> {{$t('device-management.return')}}
            </b-button>
          </div>
        </div>
      </div>

    </b-card>

    <b-modal centered id="modal-inactive" ref="modal-inactive" :title="$t('system-setting.prompt')">
      {{$t('device-management.device-table.make-inactive-prompt')}}
      <template slot="modal-footer">
        <b-button variant="primary" @click="updateItemStatus('1000000702')" class="mr-1">
          {{$t('system-setting.ok')}}
        </b-button>
        <b-button variant="danger" @click="hideModal('modal-inactive')">{{$t('system-setting.cancel')}}
        </b-button>
      </template>
    </b-modal>

    <b-modal centered id="modal-delete" ref="modal-delete" :title="$t('system-setting.prompt')">
      {{$t('device-management.device-table.delete-prompt')}}
      <template slot="modal-footer">
        <b-button variant="primary" @click="removeItem()" class="mr-1">
          {{$t('system-setting.ok')}}
        </b-button>
        <b-button variant="danger" @click="hideModal('modal-delete')">{{$t('system-setting.cancel')}}
        </b-button>
      </template>
    </b-modal>
  </div>
</template>

<script>
  import {apiBaseUrl} from '../../../constants/config'
  import Vuetable from '../../../components/Vuetable2/Vuetable'
  import VuetablePaginationBootstrap from '../../../components/Common/VuetablePaginationBootstrap'
  import {responseMessages} from '../../../constants/response-messages';
  import {downLoadFileFromServer, getApiManager, getDateTimeWithFormat, printFileFromServer} from '../../../api';
  import {validationMixin} from 'vuelidate';
  import {checkPermissionItem, getDicDataByDicIdForOptions} from "../../../utils";

  const {required} = require('vuelidate/lib/validators');
  //todo need to remove after applying dictionaly
  let getManufacturerName = (options, value) => {
    let name = null;
    if (options == null || options.length === 0)
      return name;
    options.forEach(option => {
      if (option.value === value)
        name = option.text;
    });
    return name;
  };
  export default {
    components: {
      'vuetable': Vuetable,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap
    },
    mounted() {
      this.getArchivesData();
      this.getCategoryData();
      this.getManufacturerOptions();
      this.$refs.vuetable.$parent.transform = this.transformTable.bind(this);
    },
    mixins: [validationMixin],
    validations: {
      mainForm: {
        deviceName: {
          required
        },
        archiveId: {
          required
        },
        deviceSerial: {
          required
        },
        deviceType: {
          required
        },
        guid: {
          required
        }
      }
    },
    data() {
      return {
        pageStatus: 'list',
        submitted: false,
        categoryData: [],
        categoryFilterData: [],
        categorySelectOptions: [],
        stateOptions: [
          {value: null, text: this.$t('permission-management.all')},
          {value: '1000000701', text: this.$t('permission-management.active')},
          {value: '1000000702', text: this.$t('permission-management.inactive')}
        ],
        deviceTypeSelectOptions: [
          {value: '1000001901', text: this.$t('device-management.device-table.device')},
          {value: '1000001902', text: this.$t('device-management.device-table.judge')},
          {value: '1000001903', text: this.$t('device-management.device-table.manual')}
        ],
        manufacturerOptions: [],
        selectedStatus: 'all',
        archivesSelectOptions: [],
        archivesData: [],
        vuetableItems: {
          apiUrl: `${apiBaseUrl}/device-management/device-table/device/get-by-filter-and-page`,
          perPage: 10,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'deviceId',
              sortField: 'deviceId',
              title: this.$t('device-management.no'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:number',
              sortField: 'deviceSerial',
              title: this.$t('device-management.device-no'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'archiveName',
              title: this.$t('device-management.device-list.template'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'status',
              sortField: 'status',
              title: this.$t('device-management.active'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (value) => {
                const dictionary = {
                  "1000000701": `<span class="text-success">${this.$t('system-setting.status-active')}</span>`,
                  "1000000702": `<span class="text-muted">${this.$t('system-setting.status-inactive')}</span>`
                };
                if (!dictionary.hasOwnProperty(value)) return '';
                return dictionary[value];
              }

            },
            {
              name: 'categoryName',
              title: this.$t('device-management.device-classify'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'manufacturerName',
              title: this.$t('device-management.manufacture'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (value) => {
                return getManufacturerName(this.manufacturerOptions,value);
              }
            },
            {
              name: 'originalModelName',
              title: this.$t('device-management.device-model'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:operating',
              title: this.$t('system-setting.operating'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '210px'
            }
          ]
        },

        filterOption: {
          deviceName: null,
          status: null,
          archiveName: null,
          categoryId: null
        },
        mainForm: {
          deviceId: 0,
          deviceSerial: '',
          deviceName: '',
          deviceType: 'device',
          archiveId: null,
          originalFactoryNumber: '',
          manufacturerDate: '',
          purchaseDate: '',
          supplier: '',
          contacts: '',
          mobile: '',
          deviceIp: '',
          guid: '',
          image: null,
          imageUrl: null,
          status: '1000000702'
        },
        archiveForm: {
          name: '',
          category: '',
          manufacturer: '',
          originalModel: '',
          number:'',
          templateName: ''
        },
        archiveDetailData : {}

      }
    },
    methods: {
      checkPermItem(value) {
        return checkPermissionItem(value);
      },
      getManufacturerOptions(){
        this.manufacturerOptions =  getDicDataByDicIdForOptions(9);
        console.log(this.manufacturerOptions);
      },
      onExportButton(){
        let checkedAll = this.$refs.vuetable.checkedAllStatus;
        let checkedIds = this.$refs.vuetable.selectedTo;
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': this.filterOption,
          'idList': checkedIds.join()
        };
        let link = `device-management/device-table/device`;
        downLoadFileFromServer(link,params,'device');
      },
      onPrintButton(){
        let checkedAll = this.$refs.vuetable.checkedAllStatus;
        let checkedIds = this.$refs.vuetable.selectedTo;
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': this.filterOption,
          'idList': checkedIds.join()
        };
        let link = `device-management/device-table/device`;
        printFileFromServer(link,params);
      },

      hideModal(modal) {
        this.$refs[modal].hide();
      },
      getArchivesData() {
        getApiManager().post(`${apiBaseUrl}/device-management/document-management/archive/get-all`, {
          type: 'with_parent'
        }).then((response) => {
          let message = response.data.message;
          let data = response.data.data;
          switch (message) {
            case responseMessages['ok']:
              this.archivesData = data;
              break;
          }
        });
      },
      //get device category data
      getCategoryData() {
        getApiManager().post(`${apiBaseUrl}/device-management/device-classify/category/get-all`, {
          type: 'with_parent'
        }).then((response) => {
          let message = response.data.message;
          let data = response.data.data;
          switch (message) {
            case responseMessages['ok']:
              this.categoryData = data;

              break;
          }
        });
      },
      getArchiveDetailData(archiveId) {
        this.archiveForm = {
          name :'',
          category: '',
          manufacturer: '',
          originalModel: '',
          number : '',
          templateName : ''
        };
        this.archiveDetailData = {};
        for (let item of this.archivesData) {
          if (item.archiveId === archiveId) {
            this.archiveForm.name = item.archivesName;
            this.archiveForm.number = item.archivesNumber;
            this.archiveForm.templateName = item.archiveTemplate.templateName;
            this.archiveForm.category = item.archiveTemplate.deviceCategory.categoryName;
            this.archiveForm.manufacturer = getManufacturerName(this.manufacturerOptions,item.archiveTemplate.manufacturer);
            this.archiveForm.originalModel = item.archiveTemplate.originalModel;
            if(this.pageStatus === 'show' || this.pageStatus === 'edit'){
              this.archiveDetailData = item.archiveTemplate.archiveIndicatorsList;
              this.archiveDetailData.forEach((item1) => {
                for (let v of item.archiveValueList){
                  item1.value = '';
                  if(v.indicatorsId === item1.indicatorsId){
                    item1.value = v.value;
                    break
                  }
                }
              });
            }
            break;
          }
        }
      },
      initialize(data = null) {
        if (data == null)
          this.mainForm = {
            deviceId: 0,
            deviceSerial: '',
            deviceName: '',
            deviceType: 'device',
            archiveId: null,
            originalFactoryNumber: '',
            manufacturerDate: '',
            purchaseDate: '',
            supplier: '',
            contacts: '',
            mobile: '',
            deviceIp: '',
            guid: '',
            image: null,
            imageUrl: null,
            status: '1000000702'
          };
        else {
          if (Object.keys(data).includes('createdBy')) { //if getting data from table , needful to processing
            for (let key in this.mainForm) {
              if (Object.keys(data).includes(key)) {
                if (key !== 'imageUrl' && key !== 'image') {
                  if(key === 'manufacturerDate' || key === 'purchaseDate')
                    this.mainForm[key] = getDateTimeWithFormat(data[key],"default");
                  else
                    this.mainForm[key] = data[key];
                }
                else if (key === 'imageUrl')
                  this.mainForm.image = data['imageUrl'] ? apiBaseUrl + data['imageUrl'] : null;
              }
            }
          }
          else
            this.mainForm = data;
        }
        this.submitted = false;
      },
      onSearchButton() {
        this.$refs.vuetable.refresh();
      },
      onResetButton() {
        this.filterOption = {
          deviceName: '',
          status: null,
          archiveName: null,
          categoryId: null
        };
      },
      onAction(value, data = null) {
        this.initialize(data);
        switch (value) {
          case 'create':
            this.pageStatus = 'create';
            break;
          case 'edit':
            this.pageStatus = 'edit';
            break;
          case 'show':
            this.pageStatus = 'show';
            break;
          case 'show-list':
            this.pageStatus = 'list';
            break;
          case 'activate':
            this.updateItemStatus('1000000701');
            break;
          case 'inactivate':
            this.$refs['modal-inactive'].show();
            break;
          case 'delete':
            this.$refs['modal-delete'].show();
            break;
        }
      },
      onFileChange(e) {
        let files = e.target.files || e.dataTransfer.files;
        if (!files.length)
          return;
        this.onCreateImage(files[0]);
      },
      onCreateImage(file) {
        this.mainForm.image = new Image();
        let reader = new FileReader();
        reader.onload = (e) => {
          this.mainForm.image = e.target.result;
        };
        reader.readAsDataURL(file);
        this.mainForm.imageUrl = file;
      },

      transformTable(response) {
        let transformed = {};
        let data = response.data;
        transformed.pagination = {
          total: data.total,
          per_page: data.per_page,
          current_page: data.current_page,
          last_page: data.last_page,
          from: data.from,
          to: data.to
        };
        transformed.data = [];
        let temp;
        for (let i = 0; i < data.data.length; i++) {
          temp = data.data[i];
          temp.archiveName = temp.archive.archivesName;
          temp.categoryName = temp.archive.archiveTemplate.deviceCategory.categoryName;
          temp.manufacturerName = temp.archive.archiveTemplate.manufacturer;
          temp.originalModelName = temp.archive.archiveTemplate.originalModel;
          transformed.data.push(temp);
        }
        return transformed
      },
      vuetableHttpFetch(apiUrl, httpOptions) {
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.vuetableItems.perPage,
          filter: this.filterOption
        });
      },
      onPaginationData(paginationData) {
        this.$refs.pagination.setPaginationData(paginationData);
      },
      onChangePage(page) {
        this.$refs.vuetable.changePage(page);
      },

      //update status
      updateItemStatus(statusValue) {
        let deviceId = this.mainForm.deviceId;
        if (deviceId === 0)
          return false;
        getApiManager()
          .post(`${apiBaseUrl}/device-management/device-table/device/update-status`, {
            deviceId: deviceId,
            status: statusValue
          })
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`device-management.document-management.status-updated-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                if (this.mainForm.deviceId > 0)
                  this.mainForm.status = statusValue;
                if (this.pageStatus === 'list')
                  this.$refs.vuetable.refresh();
                break;

            }
          })
          .catch((error) => {
          });
        this.$refs['modal-inactive'].hide();
      },
      //remove archives
      removeItem() {
        let deviceId = this.mainForm.deviceId;
        if (deviceId === 0)
          return false;
        getApiManager()
          .post(`${apiBaseUrl}/device-management/device-table/device/delete`, {
            deviceId: deviceId,
          })
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`device-management.device-table.deleted-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                this.pageStatus = 'list';
                this.$refs.vuetable.refresh();
                if (this.mainForm.deviceId > 0)
                  initialize();
                break;
              case responseMessages['has-devices']: // okay
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-management.device-table.has-devices`), {
                  duration: 3000,
                  permanent: false
                });
                break;
            }
          })
          .catch((error) => {
          });
        this.$refs['modal-delete'].hide();
      },
      //save device
      saveDeviceItem() {
        this.submitted = true;
        this.$v.mainForm.$touch();
        if (this.$v.mainForm.$invalid) {
          return;
        }
        const formData = new FormData();
        for (let key in this.mainForm) {
          if (key !== 'imageUrl' && key !== 'image' && key !== 'archiveValueList')
            formData.append(key, this.mainForm[key]);
          else if (key === 'imageUrl' && this.mainForm['image'] !== null && this.mainForm['imageUrl'] !== null)
            formData.append('imageUrl', this.mainForm['imageUrl'], this.mainForm['imageUrl'].name);
        }
        let finalLink = this.mainForm.deviceId > 0 ? 'modify' : 'create';
        getApiManager()
          .post(`${apiBaseUrl}/device-management/device-table/device/` + finalLink, formData)
          .then((response) => {
            let message = response.data.message;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`device-management.device-table.added-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                this.pageStatus = 'list';
                break;
              case responseMessages['used-device-name']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-device-name`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['used-device-serial']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-device-serial`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['used-device-guid']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-device-guid`), {
                  duration: 3000,
                  permanent: false
                });
                break;
            }
          })
          .catch((error) => {
          });
      }


    },
    watch: {
      'vuetableItems.perPage': function (newVal) {
        this.$refs.vuetable.refresh();
      },

      categoryData(newVal, oldVal) { // maybe called when the org data is loaded from server

        this.categorySelectOptions = [];
        if (newVal.length === 0) {
          this.categorySelectOptions.push({
            value: 0,
            html: `${this.$t('system-setting.none')}`
          });
        }
        else {
          this.categorySelectOptions = newVal.map(site => ({
            text: site.categoryName,
            value: site.categoryId
          }));
        }
        this.categoryFilterData = JSON.parse(JSON.stringify(this.categorySelectOptions));
        this.categoryFilterData.push({value: null, text: `${this.$t('permission-management.all')}`})
      },
      archivesData: function (newVal) {
        this.archivesSelectOptions = [];
        if (newVal.length === 0) {
          this.archivesSelectOptions.push({
            value: null,
            html: `${this.$t('system-setting.none')}`
          });
        }
        else {
          this.archivesSelectOptions = newVal.map(item => ({
            text: item.archivesName,
            value: item.archiveId
          }));
        }
      },
      'mainForm.archiveId': function (newVal) {
        this.getArchiveDetailData(newVal);
      }
    }
  }
</script>

