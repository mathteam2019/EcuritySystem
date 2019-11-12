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
        <b-row class="pt-4">
          <b-col cols="6">
            <b-row>
              <b-col>
                <b-form-group :label="$t('device-management.filename')">
                  <b-form-input></b-form-input>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group :label="$t('device-management.active')">
                  <b-form-select :options="stateOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group :label="$t('device-management.device-classify')">
                  <b-form-select :options="deviceClassifyData" plain/>
                </b-form-group>
              </b-col>
            </b-row>
          </b-col>
          <b-col cols="6" class="d-flex justify-content-end align-items-center">
            <div>
              <b-button size="sm" class="ml-2" variant="info default" @click="onSearchButton()">
                <i class="icofont-search-1"></i>&nbsp;{{ $t('permission-management.search') }}
              </b-button>
              <b-button size="sm" class="ml-2" variant="info default" @click="onResetButton()">
                <i class="icofont-ui-reply"></i>&nbsp;{{$t('permission-management.reset') }}
              </b-button>
              <b-button size="sm" class="ml-2" variant="outline-info default">
                <i class="icofont-share-alt"></i>&nbsp;{{ $t('permission-management.export') }}
              </b-button>
              <b-button size="sm" class="ml-2" variant="outline-info default">
                <i class="icofont-printer"></i>&nbsp;{{ $t('permission-management.print') }}
              </b-button>
              <b-button size="sm" class="ml-2" @click="onAction('create')" variant="success default">
                <i class="icofont-plus"></i>&nbsp;{{$t('permission-management.new') }}
              </b-button>
            </div>
          </b-col>
        </b-row>

        <b-row class="flex-grow-1">
          <b-col cols="12">
            <div class="table-wrapper table-responsive">
              <vuetable
                ref="vuetable"
                :api-mode="false"
                :fields="vuetableItems.fields"
                :data-manager="dataManager"
                :per-page="vuetableItems.perPage"
                pagination-path="pagination"
                @vuetable:pagination-data="onPaginationData"
                class="table-striped"
              >
                <div slot="number" slot-scope="props">
                  <span class="cursor-p text-primary" @click="onAction('edit')">{{ props.rowData.number }}</span>
                </div>
                <div slot="operating" slot-scope="props">
                  <b-button @click="onAction('edit')"
                            size="sm"
                            variant="primary default btn-square"
                  >
                    <i class="icofont-edit"></i>
                  </b-button>
                  <b-button
                    v-if="props.rowData.status=='inactive'"
                    size="sm"
                    variant="success default btn-square"
                  >
                    <i class="icofont-check-circled"></i>
                  </b-button>
                  <b-button
                    v-if="props.rowData.status=='active'"
                    size="sm"
                    variant="warning default btn-square"
                  >
                    <i class="icofont-ban"></i>
                  </b-button>
                  <b-button
                    size="sm"
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
                  <b-form-input v-model="mainForm.number"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group>
                  <template slot="label">{{$t('device-management.device')}}<span class="text-danger">*</span>
                  </template>
                  <b-form-input v-model="mainForm.name"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group>
                  <template slot="label">{{$t('device-management.device-list.archive')}}<span
                    class="text-danger">*</span>
                  </template>
                  <b-form-select v-model="mainForm.templateId" :options="templateOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group>
                  <template slot="label">{{$t('device-management.device-classify')}}<span class="text-danger">*</span>
                  </template>
                  <b-form-input v-model="mainForm.deviceClassify"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.manufacture')">
                  <label class="input-label">同方威视</label>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.origin-model')">
                  <label class="input-label">MW1000AA</label>
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
            <b-row v-if="mainForm.templateId==='waveSecurityDevice'">
              <b-col cols="4">
                <b-form-group :label="$t('device-management.device-list.original-number')">
                  <b-form-input ></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.device-list.production-date')">
                  <b-form-input ></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.device-list.purchase-date')">
                  <b-form-input ></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.device-list.supplier')">
                  <b-form-select v-model="deviceForm.supplier" :options="supplierOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.device-list.supplier-contact')">
                  <b-form-input ></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.device-list.supplier-contact-information')">
                  <b-form-input ></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.device-list.ip')">
                  <b-form-input ></b-form-input>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row v-if="mainForm.templateId!=='waveSecurityDevice'">
              <b-col cols="4">
                <b-form-group :label="$t('device-management.archive.battery-capacity')">
                  <b-form-input ></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.archive.running-memory')">
                  <b-form-input ></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.archive.screen-size')">
                  <b-form-input ></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.archive.storage-capacity')">
                  <b-form-input ></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.archive.front-camera')">
                  <b-form-input ></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.archive.rear-camera')">
                  <b-form-input ></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.archive.scalable-capacity')">
                  <b-form-input ></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.archive.operating-system')">
                  <b-form-input ></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.archive.cpu-model')">
                  <b-form-input ></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.archive.dimension')">
                  <b-form-input ></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group :label="$t('device-management.archive.body-weight')">
                  <b-form-input ></b-form-input>
                </b-form-group>
              </b-col>
            </b-row>
          </b-col>
          <b-col cols="4" class="d-flex flex-column align-items-center">
            <div class="img-wrapper">
              <img src="../../../assets/img/device.png">
              <div class="position-absolute" style="bottom: -18%;left: -41%">
                <img src="../../../assets/img/active_stamp.png">
              </div>
            </div>
            <input type="file" ref="imgFile" @change="onFileChange" style="display: none"/>
            <b-button @click="$refs.imgFile.click()" class="mt-3" variant="info skyblue default" size="sm">{{
              $t('permission-management.upload-image')}}
            </b-button>
          </b-col>
          <b-col cols="12 d-flex align-items-end justify-content-end mt-3">
            <div>
              <b-button size="sm" variant="info default"><i class="icofont-save"></i> {{$t('device-management.save')}}
              </b-button>
              <b-button size="sm" variant="success default"><i class="icofont-check-circled"></i>
                {{$t('device-management.active')}}
              </b-button>
              <b-button size="sm" variant="danger default"><i class="icofont-bin"></i>
                {{$t('device-management.delete')}}
              </b-button>
              <b-button size="sm" variant="info default" @click="onAction('show-list')"><i
                class="icofont-long-arrow-left"></i> {{$t('device-management.return')}}
              </b-button>
            </div>
          </b-col>
        </b-row>
      </div>
      <div v-if="pageStatus==='show'" class="h-100 d-flex flex-grow-1 flex-column pb-3">
        <b-tabs  class="sub-tabs" nav-class="separator-tabs ml-0" content-class="tab-content"
                 :no-fade="true">
          <b-tab :title="$t('device-management.device-table.device-info')">
            <b-row class="h-100 form-section">
              <b-col cols="8">
                <b-row>
                  <b-col cols="4">
                    <b-form-group>
                      <template slot="label">{{$t('device-management.device-no')}}<span class="text-danger">*</span>
                      </template>
                      <label class="input-label">A000</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group>
                      <template slot="label">{{$t('device-management.device')}}<span class="text-danger">*</span>
                      </template>
                      <label class="input-label">MW毫米波安检仪000</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group>
                      <template slot="label">{{$t('device-management.device-list.archive')}}<span
                        class="text-danger">*</span>
                      </template>
                      <label class="input-label">MW毫米波安检仪</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group>
                      <template slot="label">{{$t('device-management.device-classify')}}<span class="text-danger">*</span>
                      </template>
                      <label class="input-label">监管查验设备 / 人体查验设备</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.manufacture')">
                      <label class="input-label">同方威视</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.device-model')">
                      <label class="input-label">MW1000AA</label>
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
                <b-row v-if="mainForm.templateId==='waveSecurityDevice'">
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.device-list.original-number')">
                      <b-form-input ></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.device-list.production-date')">
                      <b-form-input ></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.device-list.purchase-date')">
                      <b-form-input ></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.device-list.supplier')">
                      <b-form-select v-model="deviceForm.supplier" :options="supplierOptions" plain/>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.device-list.supplier-contact')">
                      <b-form-input ></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.device-list.supplier-contact-information')">
                      <b-form-input ></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.device-list.ip')">
                      <b-form-input ></b-form-input>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row v-if="mainForm.templateId!=='waveSecurityDevice'">
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.archive.battery-capacity')">
                      <b-form-input ></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.archive.running-memory')">
                      <b-form-input ></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.archive.screen-size')">
                      <b-form-input ></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.archive.storage-capacity')">
                      <b-form-input ></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.archive.front-camera')">
                      <b-form-input ></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.archive.rear-camera')">
                      <b-form-input ></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.archive.scalable-capacity')">
                      <b-form-input ></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.archive.operating-system')">
                      <b-form-input ></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.archive.cpu-model')">
                      <b-form-input ></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.archive.dimension')">
                      <b-form-input ></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.archive.body-weight')">
                      <b-form-input ></b-form-input>
                    </b-form-group>
                  </b-col>
                </b-row>
              </b-col>
              <b-col cols="4" class="d-flex flex-column align-items-center">
                <div class="img-wrapper">
                  <img src="../../../assets/img/device.png">
                  <div class="position-absolute" style="bottom: -18%;left: -41%">
                    <img src="../../../assets/img/active_stamp.png">
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
                      <label class="input-label">0000</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group>
                      <template slot="label">{{$t('device-management.filename')}}<span class="text-danger">*</span>
                      </template>
                      <label class="input-label">MW毫米波安检仪</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group>
                      <template slot="label">{{$t('device-management.template-name')}}<span class="text-danger">*</span>
                      </template>
                      <label class="input-label">毫米波安检仪</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group>
                      <template slot="label">{{$t('device-management.device-classify')}}<span class="text-danger">*</span>
                      </template>
                      <label class="input-label">监管查验设备 / 人体查验设备</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.manufacture')">
                      <label class="input-label">同方威视</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.origin-model')">
                      <label class="input-label">MW1000AA</label>
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
                <b-row v-if="mainForm.templateId==='waveSecurityDevice'">
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.archive.inspection-method')">
                      <label class="input-label">非接触式</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.archive.single-scan-time')">
                      <label class="input-label">2</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.archive.number-of-operator')">
                      <label class="input-label">1</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.archive.detectable-item-type')">
                      <label class="input-label">金属，非金属，爆炸物</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.archive.automatic-identification')">
                      <label class="input-label">有</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.archive.privacy-protection')">
                      <label class="input-label">有</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.archive.equipment-size')">
                      <label class="input-label">2400*1400*1706</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.archive.channel-size')">
                      <label class="input-label">2200*750*1188</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.archive.equipment-weight')">
                      <label class="input-label">550</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.archive.power-by')">
                      <label class="input-label">110/220，50/60</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.archive.rated-power')">
                      <label class="input-label">2200*750*1188</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.archive.operating-temperature-humidity')">
                      <label class="input-label">0-40，0-93</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.archive.storage-temperature-humidity')">
                      <label class="input-label">0-40，0-93</label>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row v-if="mainForm.templateId!=='waveSecurityDevice'">
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.archive.battery-capacity')">
                      <b-form-input ></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.archive.running-memory')">
                      <b-form-input ></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.archive.screen-size')">
                      <b-form-input ></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.archive.storage-capacity')">
                      <b-form-input ></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.archive.front-camera')">
                      <b-form-input ></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.archive.rear-camera')">
                      <b-form-input ></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.archive.scalable-capacity')">
                      <b-form-input ></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.archive.operating-system')">
                      <b-form-input ></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.archive.cpu-model')">
                      <b-form-input ></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.archive.dimension')">
                      <b-form-input ></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group :label="$t('device-management.archive.body-weight')">
                      <b-form-input ></b-form-input>
                    </b-form-group>
                  </b-col>
                </b-row>
              </b-col>
              <b-col cols="4" class="d-flex flex-column align-items-center">
                <div class="img-wrapper">
                  <img  src="../../../assets/img/device.png">
                  <div class="position-absolute" style="bottom: -18%;left: -41%">
                    <img src="../../../assets/img/active_stamp.png">
                  </div>
                </div>
                <input type="file" ref="imgFile" @change="onFileChange" style="display: none"/>
                <b-button  class="mt-3" variant="info skyblue default" size="sm">{{
                  $t('permission-management.upload-image')}}
                </b-button>
              </b-col>
            </b-row>
          </b-tab>
        </b-tabs>
        <div class="d-flex align-items-end justify-content-end flex-grow-1 position-absolute" style="right: 30px;bottom: 30px;">
          <div>

            <b-button size="sm" variant="warning default"><i class="icofont-check-circled"></i>
              {{$t('device-management.active')}}
            </b-button>
            <b-button size="sm" variant="info default" @click="onAction('show-list')"><i
              class="icofont-long-arrow-left"></i> {{$t('device-management.return')}}
            </b-button>
          </div>
        </div>
      </div>

    </b-card>

  </div>
</template>

<script>
  import _ from 'lodash';
  import Vuetable from 'vuetable-2/src/components/Vuetable'
  import VuetablePagination from 'vuetable-2/src/components/VuetablePagination'
  import VuetablePaginationBootstrap from '../../../components/Common/VuetablePaginationBootstrap'

  export default {
    components: {
      'vuetable': Vuetable,
      'vuetable-pagination': VuetablePagination,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap
    },
    data() {
      return {
        pageStatus: 'list',
        fileData: [
          '档案-1',
          '档案-2',
          '档案-3'
        ],
        deviceClassifyData: [
          '全部',
          '监管查验设备',
          '单兵设备',
        ],
        selectedStatus: 'all',
        vuetableItems: {
          perPage: 5,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'no',
              sortField: 'no',
              title: this.$t('device-management.no'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:number',
              sortField: 'device-no',
              title: this.$t('device-management.device-no'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'archive',
              sortField: 'archive',
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
                  "active": `<span class="text-success">${this.$t('system-setting.status-active')}</span>`,
                  "inactive": `<span class="text-muted">${this.$t('system-setting.status-inactive')}</span>`
                };
                if (!dictionary.hasOwnProperty(value)) return '';
                return dictionary[value];
              }
            },
            {
              name: 'classify',
              sortField: 'classify',
              title: this.$t('device-management.device-classify'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'manufacturer',
              sortField: 'manufacturer',
              title: this.$t('device-management.manufacture'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'origin-no',
              sortField: 'origin-no',
              title: this.$t('device-management.device-model'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:operating',
              title: this.$t('system-setting.operating'),
              titleClass: 'text-center',
              dataClass: 'text-center btn-actions'
            }
          ]
        },
        tempData: [
          {
            "no": 1,
            "number": "0000",
            "device-name": "首都机场",
            "setting": null,
            "status": "active",
            "archive": null,
            "classify": null,
            "manufacturer": "张三",
            "origin-no": "13800001234",
            "remarks": "",
          },
          {
            "no": 2,
            "device-no": "0000",
            "device-name": "首都机场",
            "setting": null,
            "status": "active",
            "archive": null,
            "classify": null,
            "manufacturer": "张三",
            "origin-no": "13800001234",
            "remarks": "",
          },
          {
            "no": 3,
            "device-no": "0000",
            "device-name": "首都机场",
            "setting": null,
            "status": "active",
            "archive": "MW毫米波安检仪",
            "classify": null,
            "manufacturer": "张三",
            "origin-no": "13800001234",
            "remarks": "",
          },
          {
            "no": 4,
            "device-no": "0000",
            "device-name": "首都机场",
            "setting": null,
            "status": "active",
            "archive": "华为M6平板",
            "classify": null,
            "manufacturer": "张三",
            "origin-no": "13800001234",
            "remarks": "",
          },
          {
            "no": 5,
            "device-no": "0000",
            "device-name": "首都机场",
            "setting": null,
            "status": "active",
            "archive": null,
            "classify": null,
            "manufacturer": "张三",
            "origin-no": "13800001234",
            "remarks": "",
          },
          {
            "no": 6,
            "device-no": "0000",
            "device-name": "首都机场",
            "setting": null,
            "status": "active",
            "archive": null,
            "classify": null,
            "manufacturer": "张三",
            "origin-no": "13800001234",
            "remarks": "",
          },
          {
            "no": 7,
            "device-no": "0000",
            "device-name": "首都机场",
            "setting": null,
            "status": "active",
            "archive": null,
            "classify": null,
            "manufacturer": "张三",
            "origin-no": "13800001234",
            "remarks": "",
          },
        ],
        stateOptions: [
          {value: null, text: this.$t('system-setting.status-all')},
          {value: "active", text: this.$t('system-setting.status-active')},
          {value: "inactive", text: this.$t('system-setting.status-inactive')},
        ],

        mainForm: {
          number: null,
          name: null,
          image: null,
          templateId: 'waveSecurityDevice',
        },
        deviceForm: {
          method: null,
          supplier: null
        },
        extraForm: {
          method: null,
        },
        archivesForm: {

          templateId: 'waveSecurityDevice',

        },
        templateOptions: [
          {value: "waveSecurityDevice", text: this.$t('device-management.device-list.wave-security-device')},
          {value: "mobileTablet", text: this.$t('device-management.device-list.huawei-m6-tablet')},
        ],
        supplierOptions: [
          '同方威视代理',
          '华为产品部'
        ],
      }
    },
    methods: {

      onSearchButton() {

      },
      onResetButton() {

      },
      onAction(value) {
        switch (value) {
          case 'create':
            this.pageStatus = 'create';
            break;
          case 'edit':
            this.pageStatus = 'show';
            break;
          case 'show-list':
            this.pageStatus = 'list';
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
        this.mainForm.portrait = file;
      },
      onPaginationData(paginationData) {
        this.$refs.pagination.setPaginationData(paginationData);
      },
      onChangePage(page) {
        this.$refs.vuetable.changePage(page);
      },
      //todo need to remove with temp data
      dataManager(sortOrder, pagination) {
        let local = this.tempData;

        // sortOrder can be empty, so we have to check for that as well
        if (sortOrder.length > 0) {
          local = _.orderBy(
            local,
            sortOrder[0].sortField,
            sortOrder[0].direction
          );
        }

        pagination = this.$refs.vuetable.makePagination(
          local.length,
          this.vuetableItems.perPage
        );

        let from = pagination.from - 1;
        let to = from + this.vuetableItems.perPage;

        return {
          pagination: pagination,
          data: _.slice(local, from, to)
        };
      }
    },
    watch: {
      'vuetableItems.perPage': function (newVal) {
        this.$refs.vuetable.refresh();
      }
    }
  }
</script>

