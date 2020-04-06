<style lang="scss">
  .col-form-label {
    margin-bottom: 1px;
  }
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
  .img-rotate{
    -ms-transform: rotate(-15deg); /* IE 9 */
    -webkit-transform: rotate(-15deg); /* Safari 3-8 */
    transform: rotate(-15deg);
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
    <b-card v-show="!isLoading" class="main-without-tab">
      <div v-show="pageStatus==='list'" class="h-100 flex-column" :class="pageStatus === 'list'?'d-flex':''">
        <b-row class="pt-2">
          <b-col cols="7">
            <b-row>
              <b-col>
                <b-form-group :label="$t('device-management.device')">
                  <b-form-input v-model="filterOption.deviceName"/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group :label="$t('device-management.active')">
                  <b-form-select v-model="filterOption.status" :options="stateOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group :label="$t('device-management.filename')">
                  <b-form-select v-model="filterOption.archiveId"
                                 :options="archivesSelectOption" plain/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group :label="$t('device-management.device-classify')">
                  <b-form-select v-model="filterOption.categoryId" :options="categoryFilterDatas" plain/>
                </b-form-group>
              </b-col>
            </b-row>
          </b-col>
          <b-col cols="5" class="d-flex justify-content-end align-items-center">
            <b-button size="sm" class="ml-2" variant="info default" @click="onSearchButton()">
              <i class="icofont-search-1"/>&nbsp;{{ $t('permission-management.search') }}
            </b-button>
            <b-button size="sm" class="ml-2" variant="info default" @click="onResetButton()">
              <i class="icofont-ui-reply"/>&nbsp;{{$t('permission-management.reset') }}
            </b-button>
            <b-button size="sm" class="ml-2" variant="outline-info default" @click="onExportButton()"
                      :disabled="checkPermItem('device_export')">
              <i class="icofont-share-alt"/>&nbsp;{{ $t('permission-management.export') }}
            </b-button>
            <b-button size="sm" class="ml-2" variant="outline-info default" @click="onPrintButton"
                      :disabled="checkPermItem('device_print')">
              <i class="icofont-printer"/>&nbsp;{{ $t('permission-management.print') }}
            </b-button>
            <b-button size="sm" class="ml-2" @click="onAction('create')" variant="success default"
                      :disabled="checkPermItem('device_create')">
              <i class="icofont-plus"/>&nbsp;{{$t('permission-management.new') }}
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
                @vuetable:checkbox-toggled="onCheckStatusChange"
                @vuetable:pagination-data="onPaginationData"
                class="table-striped"
              >
                <div slot="number" slot-scope="props">
                  <span class="cursor-p text-primary" @click="onAction('show',props.rowData)">{{ props.rowData.deviceSerial }}</span>
                </div>
                <div slot="operating" slot-scope="props">
                  <b-button :disabled="checkPermItem('device_modify')"
                            @click="onAction('edit',props.rowData)"
                            size="sm"
                            variant="primary default btn-square"
                  >
                    <i class="icofont-edit"/>
                  </b-button>
                  <b-button
                    v-if="props.rowData.status==='1000000702'"
                    size="sm" @click="onAction('activate',props.rowData)"
                    variant="success default btn-square" :disabled="checkPermItem('device_update_status')"
                  >
                    <i class="icofont-check-circled"/>
                  </b-button>
                  <b-button @click="onAction('inactivate',props.rowData)"
                            v-if="props.rowData.status==='1000000701'"
                            size="sm"
                            variant="warning default btn-square" :disabled="checkPermItem('device_update_status')"
                  >
                    <i class="icofont-ban"/>
                  </b-button>
                  <b-button @click="onAction('delete',props.rowData)"
                            size="sm"
                            :disabled="props.rowData.status === '1000000701' || checkPermItem('device_delete')"
                            variant="danger default btn-square"
                  >
                    <i class="icofont-bin"/>
                  </b-button>
                </div>
              </vuetable>
            </div>
            <div class="pagination-wrapper">
              <vuetable-pagination-bootstrap
                ref="pagination"
                @vuetable-pagination:change-page="onChangePage"
                @onUpdatePerPage="vuetableItems.perPage = Number($event)"
              />
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
                  <b-form-input v-model="mainForm.deviceSerial"
                                :state="!$v.mainForm.deviceSerial.$dirty ? null : !$v.mainForm.deviceSerial.$invalid"/>
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
                  <b-form-input v-model="mainForm.deviceName"
                                :state="!$v.mainForm.deviceName.$dirty ? null : !$v.mainForm.deviceName.$invalid"/>
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
                  <b-form-select v-model="mainForm.archiveId"
                                 :state="!$v.mainForm.archiveId.$dirty ? null : !$v.mainForm.archiveId.$invalid"
                                 :options="archivesSelectOptions" plain/>
                  <div class="invalid-feedback d-block">
                    {{ (submitted && !$v.mainForm.archiveId.required) ?
                    $t('device-management.device-classify-item.field-is-mandatory') :"&nbsp;"}}
                  </div>
                </b-form-group>
              </b-col>
              <b-col cols="4" v-if="mainForm.archiveId>0">
                <b-form-group>
                  <template slot="label">{{$t('device-management.device-classify')}}<span class="text-danger">*</span>
                  </template>
                  <label class="input-label">{{archiveForm.category}}</label>
                </b-form-group>
              </b-col>
              <b-col cols="4" v-if="mainForm.archiveId>0">
                <b-form-group :label="$t('device-management.manufacture')">
                  <label class="input-label">{{archiveForm.manufacturer}}</label>
                </b-form-group>
              </b-col>
              <b-col cols="4" v-if="mainForm.archiveId>0">
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
                  <template slot="label">{{$t('device-management.device-table.guid')}}<span
                    class="text-danger">*</span>
                  </template>
                  <b-form-input v-model="mainForm.guid"
                                :state="(!$v.mainForm.guid.$dirty ? null : !$v.mainForm.guid.$invalid) && invalidGuid"/>
                  <div class="invalid-feedback d-block">
                    {{ (submitted && !$v.mainForm.guid.required) ?
                    $t('device-management.device-classify-item.field-is-mandatory') :"&nbsp;"}}
                  </div>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.device-list.original-number')">
                  <b-form-input v-model="mainForm.originalFactoryNumber"/>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.device-list.production-date')">
                  <b-form-input type="date" v-model="mainForm.manufacturerDate"/>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.device-list.purchase-date')">
                  <b-form-input type="date" v-model="mainForm.purchaseDate"/>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.device-list.supplier')">
                  <b-form-select v-model="mainForm.supplier" :options="manufacturerOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.device-list.supplier-contact')">
                  <b-form-input v-model="mainForm.contacts"/>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.device-list.supplier-contact-information')">
                  <b-form-input v-model="mainForm.mobile" :placeholder="'000-0000-0000'"
                                :state="!$v.mainForm.mobile.$dirty ? null : !$v.mainForm.mobile.$invalid"/>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.device-list.ip')">
                  <b-form-input v-model="mainForm.deviceIp"/>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.device-list.software-version')">
                  <b-form-input v-model="mainForm.softwareVersion"/>
                </b-form-group>
              </b-col>
              <b-col cols="4">
              <b-form-group :label="$t('device-management.device-list.algorithm-version')">
                <b-form-input v-model="mainForm.algorithmVersion"/>
              </b-form-group>
              </b-col>

            </b-row>
          </b-col>
          <b-col cols="4" class="d-flex flex-column align-items-center">
            <div class="img-wrapper">
              <img v-if="mainForm.image!=null&&mainForm.image!==''" :src="mainForm.image"/>
              <img v-else-if="!(mainForm.image!=null&&mainForm.image!=='')"
                   src="../../../assets/img/device.png">
              <div v-if="getLocale()==='zh'" class="position-absolute" style="bottom: -18%;left: -41%">
                <img v-if="mainForm.status === '1000000701'" src="../../../assets/img/active_stamp.png">
                <img v-else-if="mainForm.status === '1000000702'" src="../../../assets/img/no_active_stamp.png">
              </div>
              <div v-if="getLocale()==='en'" class="position-absolute" style="bottom: -18%;left: -41%">
                <img v-if="mainForm.status === '1000000702'" src="../../../assets/img/no_active_stamp_en.png" class="img-rotate">
                <img v-else-if="mainForm.status === '1000000701'" src="../../../assets/img/active_stamp_en.png" class="img-rotate">
              </div>
            </div>
            <input type="file" ref="imgFile" @change="onFileChange" style="display: none"/>
            <b-button @click="$refs.imgFile.click()" class="mt-3" variant="info skyblue default" size="sm">{{
              $t('permission-management.upload-image')}}
            </b-button>
          </b-col>
          <b-col cols="12 d-flex align-items-end justify-content-end mt-3">
            <div>
              <b-button size="sm" @click="saveDeviceItem()" variant="info default"><i class="icofont-save"></i>
                {{$t('device-management.save')}}
              </b-button>

              <b-button size="sm" variant="info default" @click="onAction('show-list')"><i
                class="icofont-long-arrow-left"/> {{$t('device-management.return')}}
              </b-button>
            </div>
          </b-col>
        </b-row>
      </div>

      <div v-if="pageStatus==='show' || pageStatus === 'edit'" class="h-100 d-flex flex-grow-1 flex-column pb-3" style="height: 100% !important;">
        <b-tabs nav-class="ml-2" :no-fade="true" style="height: 100% !important;">
          <!--          <b-tabs class="sub-tabs" nav-class="separator-tabs ml-0" content-class="tab-content"-->
          <!--                  :no-fade="true">-->
          <b-tab :title="$t('device-management.device-table.device-info')" style="height: 100% !important;">
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
                <b-row>
                  <b-col cols="4">
                    <b-form-group>
                      <template slot="label">{{$t('device-management.device-table.guid')}}<span
                        class="text-danger">*</span>
                      </template>
                      <b-form-input v-model="mainForm.guid"
                                    :state="!$v.mainForm.guid.$dirty ? null : !$v.mainForm.guid.$invalid"/>
                      <div v-if="pageStatus === 'edit'" class="invalid-feedback d-block">
                        {{ (submitted && !$v.mainForm.guid.required) ?
                        $t('device-management.device-classify-item.field-is-mandatory') :"&nbsp;"}}
                      </div>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.device-list.original-number')">
                      <b-form-input v-model="mainForm.originalFactoryNumber"/>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.device-list.production-date')">
                      <b-form-input type="date" v-model="mainForm.manufacturerDate"/>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.device-list.purchase-date')">
                      <b-form-input type="date" v-model="mainForm.purchaseDate"/>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.device-list.supplier')">
                      <b-form-select v-model="mainForm.supplier" :options="manufacturerOptions" plain/>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.device-list.supplier-contact')">
                      <b-form-input v-model="mainForm.contacts"/>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.device-list.supplier-contact-information')">
                      <b-form-input v-model="mainForm.mobile" :placeholder="'000-0000-0000'"
                                    :state="!$v.mainForm.mobile.$dirty ? null : !$v.mainForm.mobile.$invalid"/>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.device-list.ip')">
                      <b-form-input v-model="mainForm.deviceIp"/>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.device-list.software-version')">
                      <b-form-input v-model="mainForm.softwareVersion"/>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.device-list.algorithm-version')">
                      <b-form-input v-model="mainForm.algorithmVersion"/>
                    </b-form-group>
                  </b-col>
                </b-row>
              </b-col>
              <b-col cols="4" class="d-flex flex-column align-items-center">
                <div class="img-wrapper">
                  <img v-if="mainForm.image!=null&&mainForm.image!==''" :src="mainForm.image"/>
                  <img v-else-if="!(mainForm.image!=null&&mainForm.image!=='')"
                       src="../../../assets/img/device.png">
                  <div v-if="getLocale()==='zh'" class="position-absolute" style="bottom: -18%;left: -41%">
                    <img v-if="mainForm.status === '1000000701'" src="../../../assets/img/active_stamp.png">
                    <img v-else-if="mainForm.status === '1000000702'" src="../../../assets/img/no_active_stamp.png">
                  </div>
                  <div v-if="getLocale()==='en'" class="position-absolute" style="bottom: -18%;left: -41%">
                    <img v-if="mainForm.status === '1000000702'" src="../../../assets/img/no_active_stamp_en.png" class="img-rotate">
                    <img v-else-if="mainForm.status === '1000000701'" src="../../../assets/img/active_stamp_en.png" class="img-rotate">
                  </div>
                </div>
                <input type="file" ref="imgFile" @change="onFileChange" style="display: none"/>
                <b-button @click="$refs.imgFile.click()" class="mt-3" variant="info skyblue default" size="sm">{{
                  $t('permission-management.upload-image')}}
                </b-button>

                  <div class="d-flex flex-column align-items-center" style="margin-top: 50px;">
                    <div>
                      <b-button size="sm" v-if="pageStatus === 'edit'" @click="saveDeviceItem()" variant="info default"><i
                        class="icofont-save"/>
                        {{$t('device-management.save')}}
                      </b-button>
                      <b-button size="sm" v-if="mainForm.status === '1000000702'"
                                :disabled="checkPermItem('device_update_status')"
                                @click="onAction('activate',mainForm)" variant="success default"><i
                        class="icofont-check-circled"/>
                        {{$t('device-management.active')}}
                      </b-button>
                      <b-button size="sm" v-if="mainForm.status === '1000000701'"
                                :disabled="checkPermItem('device_update_status')"
                                @click="onAction('inactivate',mainForm)" variant="warning default"><i class="icofont-ban"/>
                        {{$t('permission-management.action-make-inactive')}}
                      </b-button>
                      <b-button size="sm" v-if="pageStatus!=='show' && mainForm.status === '1000000702'"
                                :disabled="checkPermItem('device_delete')"
                                @click="onAction('delete',mainForm)" variant="danger default"><i class="icofont-bin"/>
                        {{$t('device-management.delete')}}
                      </b-button>
                      <b-button size="sm" variant="info default" @click="onAction('show-list')"><i
                        class="icofont-long-arrow-left"/> {{$t('device-management.return')}}
                      </b-button>
                    </div>
                  </div>

              </b-col>

            </b-row>
          </b-tab>
          <b-tab :title="$t('device-management.device-table.archive-info')" style="height: 100% !important;">
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
                <b-row>
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
                  <div v-if="getLocale()==='zh'" class="position-absolute" style="bottom: -18%;left: -41%">
                    <img v-if="mainForm.status === '1000000701'" src="../../../assets/img/active_stamp.png">
                    <img v-else-if="mainForm.status === '1000000702'" src="../../../assets/img/no_active_stamp.png">
                  </div>
                  <div v-if="getLocale()==='en'" class="position-absolute" style="bottom: -18%;left: -41%">
                    <img v-if="mainForm.status === '1000000702'" src="../../../assets/img/no_active_stamp_en.png" class="img-rotate">
                    <img v-else-if="mainForm.status === '1000000701'" src="../../../assets/img/active_stamp_en.png" class="img-rotate">
                  </div>
                </div>
                <input type="file" ref="imgFile" @change="onFileChange" style="display: none"/>
                <b-button class="mt-3" variant="info skyblue default" size="sm">{{
                  $t('permission-management.upload-image')}}
                </b-button>

                  <div style="margin-top: 50px; margin-left: 60px;">
                    <div>
                      <b-button size="sm" v-if="pageStatus === 'edit'" @click="saveDeviceItem()" variant="info default"><i
                        class="icofont-save"/>
                        {{$t('device-management.save')}}
                      </b-button>
                      <b-button size="sm" v-if="mainForm.status === '1000000702'"
                                :disabled="checkPermItem('device_update_status')"
                                @click="onAction('activate',mainForm)" variant="success default"><i
                        class="icofont-check-circled"/>
                        {{$t('device-management.active')}}
                      </b-button>
                      <b-button size="sm" v-if="mainForm.status === '1000000701'"
                                :disabled="checkPermItem('device_update_status')"
                                @click="onAction('inactivate',mainForm)" variant="warning default"><i class="icofont-ban"/>
                        {{$t('permission-management.action-make-inactive')}}
                      </b-button>
                      <b-button size="sm" v-if="pageStatus!=='show' && mainForm.status === '1000000702'"
                                :disabled="checkPermItem('device_delete')"
                                @click="onAction('delete',mainForm)" variant="danger default"><i class="icofont-bin"/>
                        {{$t('device-management.delete')}}
                      </b-button>
                      <b-button size="sm" variant="info default" @click="onAction('show-list')"><i
                        class="icofont-long-arrow-left"/> {{$t('device-management.return')}}
                      </b-button>
                    </div>
                  </div>

              </b-col>

            </b-row>
          </b-tab>
        </b-tabs>
<!--        <div class="d-flex align-items-end justify-content-end flex-grow-1 position-absolute"-->
<!--             style="right: 30px;bottom: 30px;">-->
<!--          <div>-->
<!--            <b-button size="sm" v-if="pageStatus === 'edit'" @click="saveDeviceItem()" variant="info default"><i-->
<!--              class="icofont-save"/>-->
<!--              {{$t('device-management.save')}}-->
<!--            </b-button>-->
<!--            <b-button size="sm" v-if="mainForm.status === '1000000702'"-->
<!--                      :disabled="checkPermItem('device_update_status')"-->
<!--                      @click="onAction('activate',mainForm)" variant="success default"><i-->
<!--              class="icofont-check-circled"/>-->
<!--              {{$t('device-management.active')}}-->
<!--            </b-button>-->
<!--            <b-button size="sm" v-if="mainForm.status === '1000000701'"-->
<!--                      :disabled="checkPermItem('device_update_status')"-->
<!--                      @click="onAction('inactivate',mainForm)" variant="warning default"><i class="icofont-ban"/>-->
<!--              {{$t('permission-management.action-make-inactive')}}-->
<!--            </b-button>-->
<!--            <b-button size="sm" v-if="pageStatus!=='show' && mainForm.status === '1000000702'"-->
<!--                      :disabled="checkPermItem('device_delete')"-->
<!--                      @click="onAction('delete',mainForm)" variant="danger default"><i class="icofont-bin"/>-->
<!--              {{$t('device-management.delete')}}-->
<!--            </b-button>-->
<!--            <b-button size="sm" variant="info default" @click="onAction('show-list')"><i-->
<!--              class="icofont-long-arrow-left"/> {{$t('device-management.return')}}-->
<!--            </b-button>-->
<!--          </div>-->
<!--        </div>-->
      </div>

    </b-card>
    <div v-show="isLoading" class="loading"></div>
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
    <b-modal centered id="modal-active" ref="modal-active" :title="$t('system-setting.prompt')">
    {{$t('device-management.device-table.make-active-prompt')}}
    <template slot="modal-footer">
      <b-button variant="primary" @click="updateItemStatus('1000000701')" class="mr-1">
        {{$t('system-setting.ok')}}
      </b-button>
      <b-button variant="danger" @click="hideModal('modal-active')">{{$t('system-setting.cancel')}}
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

    <b-modal centered id="model-export" ref="model-export">
      <b-row>
        <b-col cols="12" class="d-flex justify-content-center">
          <h3 class="text-center font-weight-bold" style="margin-bottom: 1rem;">{{ $t('permission-management.export')
            }}</h3>
        </b-col>
      </b-row>
      <b-row style="height : 100px;">
        <b-col style="margin-top: 1rem; margin-left: 6rem; margin-right: 6rem;">
          <b-form-group class="mw-100 w-100" :label="$t('permission-management.export')">
            <v-select v-model="fileSelection" :options="fileSelectionOptions"
                      :state="!$v.fileSelection.$invalid" :searchable="false"
                      class="v-select-custom-style" :dir="direction" multiple/>
          </b-form-group>
        </b-col>
      </b-row>
      <div slot="modal-footer">
        <b-button size="sm" variant="orange default" @click="onExport()">
          <i class="icofont-gift"/>
          {{ $t('permission-management.export') }}
        </b-button>
        <b-button size="sm" variant="light default" @click="hideModal('model-export')">
          <i class="icofont-long-arrow-left"/>{{$t('system-setting.cancel')}}
        </b-button>
      </div>
    </b-modal>
    <Modal
      ref="exportModal"
      v-if="isModalVisible"
      :link="link" :params="params" :name="name"
      @close="closeModal"
    />
  </div>
</template>

<script>
  import {apiBaseUrl} from '../../../constants/config'
  import Vuetable from '../../../components/Vuetable2/Vuetable'
  import VuetablePaginationBootstrap from '../../../components/Common/VuetablePaginationBootstrap'
  import {responseMessages} from '../../../constants/response-messages';
  import {
    downLoadFileFromServer,
    getApiManager,
    getDateTimeWithFormat,
    isPhoneValid, isGuidValid,
    printFileFromServer, getApiManagerError
  } from '../../../api';
  import {validationMixin} from 'vuelidate';
  import {checkPermissionItem, getDicDataByDicIdForOptions, getDirection, getLocale} from "../../../utils";
  import vSelect from 'vue-select'
  import 'vue-select/dist/vue-select.css'
  import Modal from '../../../components/Modal/modal'

  const {required, minLength, maxLength} = require('vuelidate/lib/validators');
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
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
      'v-select': vSelect,
      Modal
    },
    mounted() {
      this.getArchivesData();
      this.getCategoryData();
      this.getManufacturerOptions();
      this.$refs.vuetable.$parent.transform = this.transformTable.bind(this);
    },
    mixins: [validationMixin],
    validations: {
      fileSelection: {
        required
      },
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
        guid: {
          isGuidValid,
          required, minLength: minLength(36), maxLength: maxLength(36),
        },
        mobile: {
          isPhoneValid
        }
      }
    },
    data() {
      return {
        isLoading: false,
        pageStatus: 'list',
        submitted: false,
        link: '',
        params: {},
        name: '',
        fileSelection: [],
        renderedCheckList:[],
        direction: getDirection().direction,
        fileSelectionOptions: [
          {value: 'docx', label: 'WORD'},
          {value: 'xlsx', label: 'EXCEL'},
          {value: 'pdf', label: 'PDF'},
        ],
        invalidGuid:true,
        randomGuid : '',
        guidString:['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'],
        isModalVisible: false,
        categoryData: [],
        categoryFilterData: [],
        categoryFilterDatas: [
          {value: null, text: this.$t('permission-management.all')},
          {value: '2', text: this.$t('log-management.device-log.judge')},
          {value: '3', text: this.$t('log-management.device-log.manual')},
          {value: '4', text: this.$t('log-management.device-log.hand')}
        ],
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
        archivesSelectOption: [],
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
              name: '__sequence',
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
              name: 'deviceName',
              title: this.$t('device-management.device'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'status',
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
              name: 'archiveName',
              title: this.$t('device-management.device-list.archive'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'categoryId',
              title: this.$t('device-management.device-classify'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (value) => {
                const dictionary = {
                  "2": `<span>${this.$t('log-management.device-log.judge')}</span>`,
                  "3": `<span>${this.$t('log-management.device-log.manual')}</span>`,
                  "4": `<span>${this.$t('log-management.device-log.hand')}</span>`
                };
                if (!dictionary.hasOwnProperty(value)) return '';
                return dictionary[value];
              }
            },
            {
              name: 'manufacturerName',
              title: this.$t('device-management.manufacture'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (value) => {
                return getManufacturerName(this.manufacturerOptions, value);
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
          archiveId: null,
          categoryId: null
        },
        mainForm: {
          deviceId: 0,
          deviceSerial: '',
          deviceName: '',
          deviceType: '1000001901',
          archiveId: null,
          originalFactoryNumber: '',
          manufacturerDate: '',
          purchaseDate: '',
          supplier: '',
          contacts: '',
          mobile: '',
          deviceIp: '',
          softwareVersion:'',
          algorithmVersion : '',
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
          number: '',
          templateName: ''
        },
        archiveDetailData: {}

      }
    },
    methods: {
      ///////////////////////////////////////////
      ////////   loading      Options ///////////
      ///////////////////////////////////////////
      getLocale() {
        return getLocale();
      },
      selectAll(value){
        this.$refs.vuetable.toggleAllCheckboxes('__checkbox', {target: {checked: value}});
        this.$refs.vuetable.isCheckAllStatus=value;
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.vuetable.uuid;
        let checkAllButton =  document.getElementById(checkBoxId);
        checkAllButton.checked = value;
      },
      selectNone(){
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.vuetable.uuid;
        let checkAllButton =  document.getElementById(checkBoxId);
        checkAllButton.checked = false;
      },
      changeCheckAllStatus(){
        let selectList = this.$refs.vuetable.selectedTo;
        let renderedList = this.renderedCheckList;
        if(selectList.length>=renderedList.length){
          let isEqual = false;
          for(let i=0; i<renderedList.length; i++){
            isEqual = false;
            for(let j=0; j<selectList.length; j++){
              if(renderedList[i]===selectList[j]) {j=selectList.length; isEqual=true}
            }
            if(isEqual===false){
              this.selectNone();
              break;
            }
            if(i===renderedList.length-1){
              this.selectAll(true);
            }
          }
        }
        else {
          this.selectNone();
        }

      },
      onCheckStatusChange(isChecked){
        if(isChecked){
          this.changeCheckAllStatus();
        }
        else {
          this.selectNone();
        }
      },
      createGuid(){
        let randGuid = '';
        for(let i=0; i<36; i++){
          if(i===8||i===13||i===18||i===23){
            randGuid = randGuid + "-";
          }
          else {
            let rand = Math.floor(Math.random() * 36);
            randGuid = randGuid + this.guidString[rand];
          }
        }
        this.mainForm.guid = randGuid;
      },
      closeModal() {
        this.isModalVisible = false;
      },
      checkPermItem(value) {
        return checkPermissionItem(value);
      },
      getManufacturerOptions() {
        this.manufacturerOptions = getDicDataByDicIdForOptions(9);
      },

      onExportButton() {
        // this.fileSelection = [];
        // this.$refs['model-export'].show();
        let checkedAll = this.$refs.vuetable.checkedAllStatus;
        let checkedIds = this.$refs.vuetable.selectedTo;
        let httpOption = this.$refs.vuetable.httpOptions;
        this.params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'locale' : getLocale(),
          'sort' : httpOption.params.sort,
          'filter': this.filterOption,
          'idList': checkedIds.join()
        };
        this.link = `device-management/device-table/device`;
        this.name = 'device';
        this.isModalVisible = true;
      },
      onExport() {
        let checkedAll = this.$refs.vuetable.checkedAllStatus;
        let checkedIds = this.$refs.vuetable.selectedTo;
        let params = {
          'locale' : getLocale(),
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': this.filterOption,
          'idList': checkedIds.join()
        };
        let link = `device-management/device-table/device`;
        if (this.fileSelection !== null) {
          downLoadFileFromServer(link, params, 'device', this.fileSelection);
          this.hideModal('model-export')
        }
      },
      onPrintButton() {
        let checkedAll = this.$refs.vuetable.checkedAllStatus;
        let checkedIds = this.$refs.vuetable.selectedTo;
        let httpOption = this.$refs.vuetable.httpOptions;
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'sort' : httpOption.params.sort,
          'filter': this.filterOption,
          'idList': checkedIds.join()
        };
        let link = `device-management/device-table/device`;
        printFileFromServer(link, params);
      },

      hideModal(modal) {
        this.$refs[modal].hide();
      },
      getArchivesData() {
        getApiManagerError().post(`${apiBaseUrl}/device-management/document-management/archive/get-all`, {
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
        getApiManagerError().post(`${apiBaseUrl}/device-management/device-classify/category/get-all`, {
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
          name: '',
          category: '',
          manufacturer: '',
          originalModel: '',
          number: '',
          templateName: ''
        };
        this.archiveDetailData = {};
        for (let item of this.archivesData) {
          if (item.archiveId === archiveId) {
            this.archiveForm.name = item.archivesName;
            this.archiveForm.number = item.archivesNumber;
            this.archiveForm.templateName = item.archiveTemplate.templateName;
            this.archiveForm.category = item.archiveTemplate.deviceCategory.categoryName;
            this.archiveForm.manufacturer = getManufacturerName(this.manufacturerOptions, item.archiveTemplate.manufacturer);
            this.archiveForm.originalModel = item.archiveTemplate.originalModel;
            if (this.pageStatus === 'show' || this.pageStatus === 'edit') {
              this.archiveDetailData = item.archiveTemplate.archiveIndicatorsList;
              this.archiveDetailData.forEach((item1) => {
                for (let v of item.archiveValueList) {
                  item1.value = '';
                  if (v.indicatorsId === item1.indicatorsId) {
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
            deviceType: '1000001901',
            archiveId: null,
            originalFactoryNumber: '',
            manufacturerDate: '',
            purchaseDate: '',
            supplier: '',
            contacts: '',
            mobile: '',
            deviceIp: '',
            softwareVersion: '',
            algorithmVersion: '',
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
                  if (key === 'manufacturerDate' || key === 'purchaseDate')
                    this.mainForm[key] = getDateTimeWithFormat(data[key], "default");
                  else
                    this.mainForm[key] = data[key];
                } else if (key === 'imageUrl')
                  this.mainForm.image = data['imageUrl'] ? data['imageUrl'] : null;
              }
            }
          } else
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
          archiveId: null,
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
            //this.updateItemStatus('1000000701');
            this.$refs['modal-active'].show();
            break;
          case 'inactivate':
            //this.updateItemStatus('1000000702');
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
	  this.renderedCheckList.push(data.data[i].deviceId);
          temp.archiveName = temp.archive.archivesName;
          temp.categoryId = temp.category.categoryId;
          temp.manufacturerName = temp.archive.archiveTemplate.manufacturer;
          temp.originalModelName = temp.archive.archiveTemplate.originalModel;
          transformed.data.push(temp);
        }
        return transformed
      },
      vuetableHttpFetch(apiUrl, httpOptions) {
        this.renderedCheckList = [];
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.vuetableItems.perPage,
          sort: httpOptions.params.sort,
          filter: this.filterOption
        });
      },
      onPaginationData(paginationData) {
        this.$refs.pagination.setPaginationData(paginationData);
	this.changeCheckAllStatus();
      },
      onChangePage(page) {
        this.$refs.vuetable.changePage(page);
	this.changeCheckAllStatus();
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

                this.$refs.vuetable.reload();
                break;
              case responseMessages['device-config-active']: // okay
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-management.device-table.device-config-active`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['has-fields']: // okay
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.has-fields`), {
                  duration: 3000,
                  permanent: false
                });
                break;

            }
          })
          .catch((error) => {
          });
        this.$refs['modal-inactive'].hide();
        this.$refs['modal-active'].hide();
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
              case responseMessages['active-device']: // okay
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-management.device-table.active-device`), {
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
          if(this.$v.mainForm.deviceSerial.$invalid){
            this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-management.device-table.device-number-input`), {
              duration: 3000,
              permanent: false
            });
            return;
          }
          if(this.$v.mainForm.deviceName.$invalid){
            this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-management.device-table.device-input`), {
              duration: 3000,
              permanent: false
            });
            return;
          }
          if(this.$v.mainForm.archiveId.$invalid){
            this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-management.device-table.archive-input`), {
              duration: 3000,
              permanent: false
            });
            return;
          }
          if(this.$v.mainForm.guid.$invalid){
            if(this.mainForm.guid==='') {
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-management.device-table.guid-input`), {
                duration: 3000,
                permanent: false
              });
              return;
            }else {
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-management.device-table.guid-valid`), {
                duration: 3000,
                permanent: false
              });
              return;
            }
          }
          if(this.$v.mainForm.mobile.$invalid){
            this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.please-enter-organization-mobile`), {
              duration: 3000,
              permanent: false
            });
            return;
          }
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
        this.isLoading = true;
        getApiManager()
          .post(`${apiBaseUrl}/device-management/device-table/device/` + finalLink, formData)
          .then((response) => {
            let message = response.data.message;
            switch (message) {
              case responseMessages['ok']: // okay
                if(finalLink === 'create') {
                  this.$notify('success', this.$t('permission-management.success'), this.$t(`device-management.device-table.added-successfully`), {
                    duration: 3000,
                    permanent: false
                  });
                }
                else {
                  this.$notify('success', this.$t('permission-management.success'), this.$t(`device-management.device-table.updated-successfully`), {
                    duration: 3000,
                    permanent: false
                  });
                }
                this.invalidGuid=true;
                this.pageStatus = 'list';
                this.$refs.vuetable.reload();
                this.isLoading = false;
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
                this.invalidGuid=false;
                break;
            }
            this.isLoading = false;
          })
          .catch((error) => {
            this.isLoading = false;
          });
      }


    },
    watch: {
      'vuetableItems.perPage': function (newVal) {
        this.$refs.vuetable.refresh();
	this.changeCheckAllStatus();
      },
      'mainForm.guid': function (newVal) {
        this.invalidGuid=true;
      },

      categoryData(newVal, oldVal) { // maybe called when the org data is loaded from server

        this.categorySelectOptions = [];
        if (newVal.length === 0) {
          this.categorySelectOptions.push({
            value: 0,
            html: `${this.$t('system-setting.none')}`
          });
        } else {
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
        this.archivesSelectOption = [];
        if (newVal.length === 0) {
          this.archivesSelectOptions.push({
            value: null,
            html: `${this.$t('system-setting.none')}`
          });
          this.archivesSelectOption.push({
            value: null,
            html: `${this.$t('system-setting.none')}`
          });
        } else {
          this.archivesSelectOptions = newVal.map(item => ({
            text: item.archivesName,
            value: item.archiveId
          }));
          this.archivesSelectOption = newVal.map(item => ({
            text: item.archivesName,
            value: item.archiveId
          }));
          this.archivesSelectOption.push({value: null, text: `${this.$t('permission-management.all')}`})
        }
      },
      'mainForm.archiveId': function (newVal) {
        this.getArchiveDetailData(newVal);
        if(this.pageStatus==='create'){
          this.createGuid();
        }
      }
    }
  }
</script>

