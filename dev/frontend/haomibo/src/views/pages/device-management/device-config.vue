<style lang="scss">
  @import '../../../assets/css/dual-list.css';

  $cyan-button-color: #178af7;
  .device-config {
    .v-select.v-select-custom-style {
      & > div {
        border-radius: 0.3rem !important;

        & > div {
          border-radius: 0.3rem !important;
        }
      }

    }
    .p-left-0 {
      padding-left: 0;
    }
    .p-right-0 {
      padding-right: 0;
    }
    div.label-center label {
      display: flex;
      align-self: center;
    }
    .switch-button {
      justify-content: flex-end;
      padding-right: 1rem;
      span {
        padding: 5px;
        border: solid 1px #cccccc;
        background: #ededed;
        height: 32px;
        width: 32px;
        text-align: center;
        cursor: pointer;
        &:first-child {
          border-right-color: transparent !important;
        }
        &:last-child {
          border-left-color: transparent;
        }
        i {
          font-size: 16px;
        }
        &.active {
          border-color: #3182eb !important;
          background: #3182eb;
          color: white;
        }
      }
    }
    .second-row {
      height: calc(100% - 4rem);
      &.list {
        margin-top: -23px !important;
        height: calc(100% - 29px);
      }
    }

    .search-box {
      div[role='group'] {
        display: flex;
      }

      .form-control {
        max-width: calc(100% - 75px);
        border-top-right-radius: 0;
        border-bottom-right-radius: 0;
      }

      .btn {
        border-top-left-radius: 0;
        border-bottom-left-radius: 0;
      }
    }

    .btn.btn-cyan {
      background-color: $cyan-button-color;
      color: white;

      &:hover {
        background-color: $cyan-button-color;
      }
    }

    .btn.btn-outline-cyan {
      border-color: #cbcbcb;
      background-color: #ededed;
    }

    .section {
      background-color: #f4f4f4;
      padding: 1rem 0 0.5rem 0 !important;
    }

    div.label-center label {
      display: flex;
      align-self: center;
    }

    .form-section .form-group.full-width {
      max-width: unset;
      .form-control {
        max-width: unset;
      }
    }

  }

  body.rtl {
    .device-config {
      .switch-button {
        padding-right: unset;
        padding-left: 1rem;
        span {
          &:first-child {
            border-right-color: #cccccc !important;
            border-left-color: transparent !important;
          }
          &:last-child {
            border-left-color: #cccccc !important;
            border-right-color: transparent !important;
          }
        }
      }
      .search-box {
        .form-control {
          max-width: calc(100% - 75px);
          border-radius: 0 0.3rem 0.3rem 0;
        }

        .btn {
          border-radius: 0.3rem 0 0 0.3rem;
          display: flex;
          align-items: center;
          i {
            padding-left: 3px;
          }
        }
      }
      .section {
        background-color: #f4f4f4;
        padding: 1rem 0 0.5rem 0 !important;
      }
      ul.tree-root {
        overflow-x: hidden;
        .tree-arrow.expanded.has-child:after {
          transform: rotate(-135deg) translateY(11%) translateX(10px)
        }
      }
      .vue-dual-list .list ul.pd {
        padding-right: 0;
      }
    }
  }
</style>
<template>
  <div class="device-config">
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb/>
        </b-colxx>
      </b-row>
    </div>
    <b-tabs v-show="!isLoading" nav-class="ml-2" :no-fade="true">
      <b-tab :title="$t('device-management.site-config')">
        <div class="switch-button d-flex mb-3">
          <span :class="`${switchStatus==='config'?'active':''}`" @click="changeSwitchStatus('config')"><i
            class="icofont-gear"/></span>
          <span :class="`${switchStatus==='list'?'active':''}`" @click="changeSwitchStatus('list')"><i
            class="icofont-listine-dots"/></span>
        </div>
        <b-row v-show="switchStatus==='config'" class="second-row">
          <b-col cols="4" class="d-flex flex-column">
            <div class="section d-flex flex-column h-100">
              <b-row class="m-0">
                <b-colxx cols="12">

                  <label class="font-weight-bold mb-3">{{$t('device-management.site')}}</label>
                  <b-form-group class="search-box">
                    <b-form-input size="sm" v-model="treeFilter"/>
                    <b-button size="sm" variant="info default">
                      <i class="icofont-search"/>
                      {{$t('device-management.search')}}
                    </b-button>
                  </b-form-group>

                </b-colxx>
                <b-colxx cols="12">
                  <tree ref="fieldTree" v-if="isLoadCompleted"
                        :filter="treeFilter"
                        :data="siteTreeData"
                        :options="treeOptions"
                        @node:selected="onNodeSelected"
                  />
                </b-colxx>
              </b-row>
            </div>
          </b-col>
          <b-col cols="8" class="d-flex flex-column " :class="direction==='ltr'?'p-left-0':'p-right-0'">
            <div class="section d-flex flex-column h-100">
              <b-row class="mx-4 flex-grow-1">
                <b-col>
                  <vue-dual-list ref="fieldSelectList" class="h-100 pb-3" :options="fieldSelectOptions"/>
                </b-col>
              </b-row>
              <b-row class="mx-4">
                <b-col cols="12" class="d-flex justify-content-end align-self-end">
                  <b-button :disabled="selectedFieldId === 0 || checkPermItem('device_field_modify')" size="sm"
                            variant="info default mr-1"
                            @click="onSaveDeviceToField()">
                    <i class="icofont-save"/>
                    {{ $t('permission-management.save-button') }}
                  </b-button>
                </b-col>
              </b-row>
            </div>
          </b-col>
        </b-row>

        <b-row v-show="switchStatus==='list'" class="second-row list">
          <b-col cols="12 d-flex flex-column">
            <b-row>
              <b-col cols="8">
                <b-row>
                  <b-col cols="3">
                    <b-form-group :label="$t('device-management.device-name')">
                      <b-form-input v-model="configFilter.deviceName"/>
                    </b-form-group>
                  </b-col>
                  <b-col cols="3">
                    <b-form-group :label="$t('maintenance-management.maintenance-task.device-classification')">
                      <b-form-select v-model="configFilter.categoryId" :options="deviceCategoryOptions"
                                     plain/>
                    </b-form-group>
                  </b-col>
                  <b-col cols="3">
                    <b-form-group :label="$t('maintenance-management.maintenance-task.position')">
                      <b-form-select v-model="configFilter.fieldId" :options="siteSelectOptions" plain/>
                    </b-form-group>
                  </b-col>
                </b-row>
              </b-col>
              <b-col cols="4" class="d-flex justify-content-end align-items-center">
                <b-button size="sm" class="ml-2" variant="info default" @click="onConfigSearchButton()">
                  <i class="icofont-search-1"/>&nbsp;{{ $t('permission-management.search') }}
                </b-button>
                <b-button size="sm" class="ml-2" variant="info default" @click="onConfigResetButton()">
                  <i class="icofont-ui-reply"/>&nbsp;{{$t('permission-management.reset') }}
                </b-button>
                <b-button size="sm" class="ml-2" variant="outline-info default" @click="onExportButton"
                          :disabled="checkPermItem('device_field_export')">
                  <i class="icofont-share-alt"/>&nbsp;
                  {{ $t('permission-management.export') }}
                </b-button>
                <b-button size="sm" class="ml-2" variant="outline-info default" @click="onPrintButton"
                          :disabled="checkPermItem('device_field_print')">
                  <i class="icofont-printer"/>&nbsp;{{ $t('permission-management.print') }}
                </b-button>
              </b-col>
            </b-row>
            <b-row class="flex-grow-1">
              <b-col cols="12">
                <div class="table-wrapper table-responsive">
                  <vuetable
                    ref="configListTable"
                    :fields="configListTableItems.fields"
                    :api-url="configListTableItems.apiUrl"
                    :http-fetch="configListTableHttpFetch"
                    :per-page="configListTableItems.perPage"
                    pagination-path="pagination"
                    track-by="deviceId"
                    @vuetable:checkbox-toggled="onCheckStatusChange"
                    @vuetable:pagination-data="onConfigTablePaginationData"
                    class="table-striped"
                  >
                  </vuetable>
                </div>
                <div class="pagination-wrapper">
                  <vuetable-pagination-bootstrap
                    ref="configListTablePagination"
                    :initial-per-page="configListTableItems.perPage"
                    @vuetable-pagination:change-page="onConfigTableChangePage"
                    @onUpdatePerPage="configListTableItems.perPage = Number($event)"
                  />
                </div>
              </b-col>
            </b-row>
          </b-col>
          <b-modal  centered id="model-export" ref="model-export">
            <b-row>
              <b-col cols="12" class="d-flex justify-content-center">
                <h3 class="text-center font-weight-bold" style="margin-bottom: 1rem;">{{ $t('permission-management.export') }}</h3>
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
        </b-row>
      </b-tab>
      <b-tab :title="$t('device-management.maintenance-config')">
        <b-row v-show="pageStatus ==='list'" class="h-100">
          <b-col cols="12 d-flex flex-column">
            <b-row class="pt-2">
              <b-col cols="8">
                <b-row>
                  <b-col cols="3">
                    <b-form-group :label="$t('device-management.device-name')">
                      <b-form-input v-model="pendingFilter.deviceName"/>
                    </b-form-group>
                  </b-col>
                  <b-col cols="3">
                    <b-form-group :label="$t('maintenance-management.maintenance-task.position')">
                      <b-form-select v-model="pendingFilter.fieldId" :options="siteSelectOptions" plain/>
                    </b-form-group>
                  </b-col>
                  <b-col cols="3">
                    <b-form-group :label="$t('personal-inspection.operation-mode')">
                      <b-form-select v-model="pendingFilter.mode" :options="operationModeOptions" plain/>
                    </b-form-group>
                  </b-col>
                </b-row>
              </b-col>
              <b-col cols="4" class="d-flex justify-content-end align-items-center">
                <b-button size="sm" class="ml-2" variant="info default" @click="onPendingSearchButton()">
                  <i class="icofont-search-1"/>&nbsp;{{ $t('permission-management.search') }}
                </b-button>
                <b-button size="sm" class="ml-2" variant="info default" @click="onPendingResetButton()">
                  <i class="icofont-ui-reply"/>&nbsp;{{$t('permission-management.reset') }}
                </b-button>
              </b-col>
            </b-row>
            <b-row class="flex-grow-1">
              <b-col cols="12">
                <div class="table-wrapper table-responsive">
                  <vuetable
                    ref="pendingListTable"
                    :fields="pendingListTableItems.fields"
                    :api-url="pendingListTableItems.apiUrl"
                    :http-fetch="pendingListTableHttpFetch"
                    :per-page="pendingListTableItems.perPage"
                    pagination-path="pagination"
                    @vuetable:checkbox-toggled="onCheckStatusChangeGroup"
                    @vuetable:pagination-data="onPendingListTablePaginationData"
                    class="table table-striped"
                  >
                    <div slot="number" slot-scope="props">
                      <span class="cursor-p text-primary"
                            @click="onAction('show', props.rowData)">{{ props.rowData.deviceSerialNumber}}</span>
                    </div>
                    <div slot="operating" slot-scope="props">
                      <b-button size="sm" variant="info default btn-square"
                                :disabled="checkPermItem('device_config_modify') || props.rowData.deviceId === 7749"
                                @click="onAction('edit',props.rowData)">
                        <i class="icofont-edit"/>
                      </b-button>
                      <b-button
                        v-if="props.rowData.status==='1000000702'"
                        size="sm" @click="onAction('activate',props.rowData)"
                        variant="success default btn-square" :disabled="checkPermItem('device_config_update_status')"
                      >
                        <i class="icofont-check-circled"/>
                      </b-button>
                      <b-button @click="onAction('inactivate',props.rowData)"
                                v-if="props.rowData.status==='1000000701'"
                                size="sm"
                                variant="warning default btn-square" :disabled="checkPermItem('device_config_update_status')"
                      >
                        <i class="icofont-ban"/>
                      </b-button>
                    </div>
                  </vuetable>
                </div>
                <div class="pagination-wrapper">
                  <vuetable-pagination-bootstrap
                    ref="pendingListTablePagination"
                    :initial-per-page="pendingListTableItems.perPage"
                    @vuetable-pagination:change-page="onPendingListTableChangePage"
                    @onUpdatePerPage="pendingListTableItems.perPage = Number($event)"
                  />
                </div>
              </b-col>
            </b-row>
          </b-col>
        </b-row>
        <b-row v-show="pageStatus !== 'list'" class="h-100 form-section">
          <b-col cols="10">
            <b-row>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.position')}}
                  </template>
                  <label>{{selectedDeviceData.fieldName}}</label>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.device-classification')}}
                  </template>
                  <label>{{selectedDeviceData.category}}</label>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.device')}}
                  </template>
                  <label>{{selectedDeviceData.deviceName}}</label>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col cols="6">
                <b-form-group class="full-width">
                  <template slot="label">
                    {{$t('device-config.maintenance-config.suitable-for')}}&nbsp;
                  </template>
                  <v-select v-model="configForm.fromDeviceId" ref="deviceSelect" :options="fromConfigDeviceSelectOptions"
                            class="v-select-custom-style" :dir="direction" :searchable="false" multiple/>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.operate-mode')}}
                  </template>
                  <b-form-select v-model="configForm.modeId" :options="modeSelectData" @change="changeDefault" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.atr-insuspicion-process')}}
                  </template>
                  <b-form-select v-model="configForm.atrSwitch" :options="atrOptions"
                                 :disabled="getModeValueFromId(configForm.modeId) !== '1000001302'" plain/>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.safety-hand-check')}}
                  </template>
                  <b-form-select v-model="configForm.manualSwitch" :options="yesNoOptions" :disabled="getModeValueFromId(configForm.modeId) === '1000001301'" @change="changeManualSwitch" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.male-guide-object')}}
                  </template>
                  <b-form-select v-model="configForm.manDeviceGender" :disabled="getModeValueFromId(configForm.modeId) === '1000001301' || configForm.manualSwitch ==='1000000602'" :options="genderFilterOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.female-guide-object')}}
                  </template>
                  <b-form-select v-model="configForm.womanDeviceGender" :disabled="getModeValueFromId(configForm.modeId) === '1000001301' || configForm.manualSwitch ==='1000000602'" :options="genderFilterOptions" plain/>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.monitor-group')}}
                  </template>
                  <v-select
                    :disabled="getModeValueFromId(configForm.modeId) !== '1000001303' && getModeValueFromId(configForm.modeId) !== '1000001304'"
                    v-model="configForm.judgeDeviceId" :options="judgeDeviceOptions" class="v-select-custom-style"
                    :dir="direction" :searchable="false" multiple/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.male-scan-object')}}
                  </template>
                  <b-form-select
                    :disabled="(getModeValueFromId(configForm.modeId)!== '1000001303' && getModeValueFromId(configForm.modeId)!== '1000001304')"
                    v-model="configForm.manRemoteGender" :options="genderFilterOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.female-scan-object')}}
                  </template>
                  <b-form-select
                    :disabled="(getModeValueFromId(configForm.modeId)!== '1000001303' && getModeValueFromId(configForm.modeId)!== '1000001304')"
                    v-model="configForm.womanRemoteGender" :options="genderFilterOptions" plain/>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.hand-check-position')}}
                  </template>
                  <v-select
                    :disabled="getModeValueFromId(configForm.modeId)!== '1000001302' && getModeValueFromId(configForm.modeId)!== '1000001304'"
                    v-model="configForm.manualDeviceId" :options="manualDeviceOptions" class="v-select-custom-style"
                    :dir="direction" :searchable="false" multiple/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.male-inspection-object')}}
                  </template>
                  <b-form-select
                    :disabled="(getModeValueFromId(configForm.modeId)!== '1000001302' && getModeValueFromId(configForm.modeId)!== '1000001304')"
                    v-model="configForm.manManualGender" :options="genderFilterOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.female-inspection-object')}}&nbsp;
                  </template>
                  <b-form-select
                    :disabled="(getModeValueFromId(configForm.modeId)!== '1000001302' && getModeValueFromId(configForm.modeId)!== '1000001304')"
                    v-model="configForm.womanManualGender" :options="genderFilterOptions" plain/>
                </b-form-group>
              </b-col>
            </b-row>
          </b-col>
          <b-col cols="2">
            <div v-if="getLocale()==='zh'" class="position-absolute" style="bottom: 0;left: 0">
              <img v-if="configForm.status === '1000000701'" src="../../../assets/img/active_stamp.png">
              <img v-else-if="configForm.status === '1000000702'" src="../../../assets/img/no_active_stamp.png">
            </div>
            <div v-if="getLocale()==='en'" class="position-absolute" style="bottom: 0;left: 0">
              <img v-if="configForm.status === '1000000702'" src="../../../assets/img/no_active_stamp_en.png" class="img-rotate">
              <img v-else-if="configForm.status === '1000000701'" src="../../../assets/img/active_stamp_en.png" class="img-rotate">
            </div>
          </b-col>
          <b-col cols="12" class="d-flex justify-content-end align-self-end">
            <div>
              <b-button variant="info default" size="sm" @click="onSaveDeviceConfig()"
                        v-if="!checkPermItem('device_config_modify') && this.pageStatus==='edit'">
                <i class="icofont-save"/> {{$t('permission-management.permission-control.save')}}
              </b-button>
              <b-button v-if="configForm.status === '1000000701'"
                        @click="onAction('inactivate',configForm)" size="sm"
                        variant="warning default" :disabled="checkPermItem('device_config_update_status')">
                <i class="icofont-ban"/> {{$t('permission-management.action-make-inactive')}}
              </b-button>
              <b-button v-if="configForm.status === '1000000702'"
                        @click="onAction('activate',configForm)" size="sm"
                        :disabled="checkPermItem('device_config_update_status')"
                        variant="success default">
                <i class="icofont-check-circled"/> {{$t('system-setting.status-active')}}
              </b-button>
              <b-button @click="onAction('list')" variant="info default" size="sm"><i
                class="icofont-long-arrow-left"/> {{
                $t('permission-management.return') }}
              </b-button>
            </div>
          </b-col>
        </b-row>
      </b-tab>
    </b-tabs>
    <div v-show="isLoading" class="loading"></div>
    <b-modal centered id="modal-inactive" ref="modal-inactive" :title="$t('system-setting.prompt')">
      {{$t('device-management.document-template.make-inactive-prompt')}}
      <template slot="modal-footer">
        <b-button variant="primary" @click="updateItemStatus('1000000702')" class="mr-1">
          {{$t('system-setting.ok')}}
        </b-button>
        <b-button variant="danger" @click="hideModal('modal-inactive')">{{$t('system-setting.cancel')}}
        </b-button>
      </template>
    </b-modal>
    <b-modal centered id="modal-active" ref="modal-active" :title="$t('system-setting.prompt')">
      {{$t('device-management.document-template.make-active-prompt')}}
      <template slot="modal-footer">
        <b-button variant="primary" @click="updateItemStatus('1000000701')" class="mr-1">
          {{$t('system-setting.ok')}}
        </b-button>
        <b-button variant="danger" @click="hideModal('modal-active')">{{$t('system-setting.cancel')}}
        </b-button>
      </template>
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

  import {apiBaseUrl} from "../../../constants/config";
  import {downLoadFileFromServer, getApiManager, getApiManagerError, printFileFromServer} from '../../../api';
  import {responseMessages} from '../../../constants/response-messages';
  import Vuetable from '../../../components/Vuetable2/Vuetable'
  import VuetablePagination from 'vuetable-2/src/components/VuetablePagination'
  import VuetablePaginationBootstrap from "../../../components/Common/VuetablePaginationBootstrap";
  import {checkPermissionItem, getDicDataByDicIdForOptions, getDirection, getLocale} from "../../../utils";
  import Vue from 'vue'
  import VueDualList from '../../../components/Duallist/VueDualList'
  import LiquorTree from 'liquor-tree'
  import vSelect from 'vue-select'
  import {validationMixin} from 'vuelidate'
  import 'vue-select/dist/vue-select.css'
  import Modal from '../../../components/Modal/modal'

  const {required, minValue, maxValue} = require('vuelidate/lib/validators');

  let getSiteFullName = orgData => {
    let orgFullName = '';
    if (orgData == null)
      return orgFullName;
    while (orgData.parent != null) {
      orgFullName = orgData.fieldDesignation + '/' + orgFullName;
      orgData = orgData.parent;
    }
    orgFullName = orgData.fieldDesignation + '/' + orgFullName;
    return orgFullName.slice(0, -1);
  };

  Vue.use(LiquorTree);
  Vue.treeFilter = '';//redefined filter option.
  export default {
    components: {
      'vuetable': Vuetable,
      'vuetable-pagination': VuetablePagination,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
      'VueDualList': VueDualList,
      'v-select': vSelect,
      Modal
    },

    mixins: [validationMixin],
    validations:{
      fileSelection : {
        required
      }
    },

    mounted() {
      this.getCategoryData();
      this.getSiteData();
      this.getModeData();
      this.getManualDeviceData();
      this.getJudgeDeviceData();
      this.getManufacturerOptions();
      this.$refs.configListTable.$parent.transform = this.transformConfigTable.bind(this);
      this.$refs.pendingListTable.$parent.transform = this.transformPendingTable.bind(this);
    },
    data() {

      return {
        // Deselect: {
        //   render: createElement => createElement('span', '❌'),
        // },
        isLoading: false,
        isLoadCompleted: false,
        modeDictionaryData: {
          '1000001301': this.$t('personal-inspection.security-instrument'),
          '1000001302': this.$t('personal-inspection.security-instrument-and-hand-test'),
          '1000001303': this.$t('personal-inspection.security-instrument-and-manual-test'),
          '1000001304': this.$t('personal-inspection.security-instrument-and-hand-test-and-device')
        },
        siteData: [],
        categoryData: [],
        siteTreeData: [],
        renderedCheckList: [],
        renderedCheckListGroup: [],
        pageStatus: 'list',
        switchStatus: 'config', // config / list
        deviceCategoryOptions: [],
        siteSelectOptions: [],
        operationModeOptions: [
          {value: null, text: this.$t('personal-inspection.all')},
          {value: '1', text: this.$t('personal-inspection.security-instrument')},
          {value: '2', text: this.$t('personal-inspection.security-instrument-and-hand-test')},
          {value: '3', text: this.$t('personal-inspection.security-instrument-and-manual-test')},
          {value: '4', text: this.$t('personal-inspection.security-instrument-and-hand-test-and-device')},
        ],
        modeData: [],
        modeSelectData: [],
        fromConfigDeviceData: [],
        fromConfigDeviceSelectOptions: [],
        availableDevicesData: [],
        devicesPerFieldData: [],
        selectedFieldId: 0,
        pendingFilter: {
          deviceName: null,
          categoryId: null,
          fieldId: null,
          mode:null
        },
        configFilter: {
          deviceName: null,
          categoryId: null,
          fieldId: null
        },
        yesNoOptions: [
          {value: '1000000601', text: this.$t('system-setting.parameter-setting.yes')},
          {value: '1000000602', text: this.$t('system-setting.parameter-setting.no')},
        ],
        atrOptions: [
          {value: 'TRUE', text: this.$t('device-config.maintenance-config.release')},
          {value: 'FALSE', text: this.$t('device-config.maintenance-config.inspection')},
        ],
        manufacturerOptions: [],
        genderFilterOptions: [
          {value: '1000000003', text: this.$t('permission-management.all')},
          {value: '1000000001', text: this.$t('permission-management.male')},
          {value: '1000000002', text: this.$t('permission-management.female')}
        ],
        selectedDeviceData: {
          fieldName: '',
          category: '',
          deviceName: ''
        },
        manualDeviceOptions: [],
        judgeDeviceOptions: [],
        selectedDeviceName: null,
        link: '',
        params: {},
        name: '',
        fileSelection : [],
        fileSelectionOptions: [
          {value: 'docx', label: 'WORD'},
          {value: 'xlsx', label: 'EXCEL'},
          {value: 'pdf', label: 'PDF'},
        ],
        isModalVisible: false,
        configForm: {
          manualDeviceId: [],
          judgeDeviceId: [],
        },
        direction: getDirection().direction,
        appliedItems: [],
        isRequired: false,
        fieldSelectOptions: {
          label: this.$t('device-management.filter'),
          inputOptions: {uppercase: false, isRequired: false},
          isLtr: getDirection().direction,
          resizeBox: "md",
          items: this.availableDevicesData,
          colorItems: '#1E90FF',
          selectedItems: []
        },
        treeFilter: '',
        treeOptions: {direction: getDirection().direction},
        pendingListTableItems: {
          apiUrl: `${apiBaseUrl}/device-management/device-config/config/get-by-filter-and-page`,
          perPage: 10,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__sequence',
              title: this.$t('maintenance-management.maintenance-task.no'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '7%',
            },
            {
              name: '__slot:number',
              sortField: 'deviceSerial',
              title: this.$t('device-management.device-no'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '13%'
            },
            {
              name: 'deviceName',
              title: this.$t('maintenance-management.maintenance-task.device'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '17%'
            },
            {
              name: 'siteNameWithParent',
              title: this.$t('device-management.site'),
              titleClass: 'text-center',
              dataClass: 'text-center',

            },
            {
              name: '__slot:operating',
              title: this.$t('system-setting.operating'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '150px'
            }
          ]
        },
        configListTableItems: {
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
              title: this.$t('maintenance-management.maintenance-task.no'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '7%'
            },
            {
              name: 'deviceSerial',
              sortField: 'deviceSerial',
              title: this.$t('device-management.device-no'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '15%'
            },
            {
              name: 'deviceName',
              title: this.$t('maintenance-management.maintenance-task.device'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '20%'
            },
            {
              name: 'deviceCategoryName',
              title: this.$t('menu.device-classify'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '25%'
            },
            {
              name: 'siteNameWithParent',
              title: this.$t('device-management.site'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
          ]
        },
      }
    },
    methods: {
      ///////////////////////////////////////////
      ////////   loading      Options ///////////
      ///////////////////////////////////////////
      getLocale() {

        return getLocale();
      },
      changeDefault(value){

        if(value===1){
          this.configForm.manRemoteGender = null;
          this.configForm.womanRemoteGender = null;
          this.configForm.manManualGender = null;
          this.configForm.womanManualGender = null;
          this.configForm.atrSwitch = null;
          this.configForm.manualSwitch = null;
          this.configForm.manDeviceGender = null;
          this.configForm.womanDeviceGender = null;
        }
        if(value===3){
          this.configForm.manRemoteGender = this.genderFilterOptions[1].value;
          this.configForm.womanRemoteGender = this.genderFilterOptions[2].value;
          this.configForm.manManualGender = null;
          this.configForm.womanManualGender = null;
          this.configForm.atrSwitch = null;
          this.configForm.manualSwitch = '1000000602';
        }
        if(value===2){
          this.configForm.manManualGender = this.genderFilterOptions[1].value;
          this.configForm.womanManualGender = this.genderFilterOptions[2].value;
          this.configForm.manRemoteGender = null;
          this.configForm.womanRemoteGender = null;
          this.configForm.atrSwitch = this.atrOptions[0].value;
          this.configForm.manualSwitch = '1000000602';
        }
        if(value===4){
          this.configForm.manRemoteGender = this.genderFilterOptions[1].value;
          this.configForm.womanRemoteGender = this.genderFilterOptions[2].value;
          this.configForm.manManualGender = this.genderFilterOptions[1].value;
          this.configForm.womanManualGender = this.genderFilterOptions[2].value;
          this.configForm.atrSwitch = null;
          this.configForm.manualSwitch = '1000000602';
        }
      },

      changeManualSwitch(value){
        if(value==='1000000602'){
          this.configForm.manDeviceGender = null;
          this.configForm.womanDeviceGender = null;
        }
        if(value==='1000000601'){
          this.configForm.manDeviceGender = '1000000001';
          this.configForm.womanDeviceGender = '1000000002';
        }
      },
      selectAll(value){
        this.$refs.configListTable.toggleAllCheckboxes('__checkbox', {target: {checked: value}});
        this.$refs.configListTable.isCheckAllStatus=value;
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.configListTable.uuid;
        let checkAllButton =  document.getElementById(checkBoxId);
        checkAllButton.checked = value;
      },
      selectNone(){
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.configListTable.uuid;
        let checkAllButton =  document.getElementById(checkBoxId);
        checkAllButton.checked = false;
      },
      changeCheckAllStatus(){
        let selectList = this.$refs.configListTable.selectedTo;
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
      selectAllGroup(value){
        this.$refs.pendingListTable.toggleAllCheckboxes('__checkbox', {target: {checked: value}});
        this.$refs.pendingListTable.isCheckAllStatus=value;
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.pendingListTable.uuid;
        let checkAllButton =  document.getElementById(checkBoxId);
        checkAllButton.checked = value;
      },
      selectNoneGroup(){
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.pendingListTable.uuid;
        let checkAllButton =  document.getElementById(checkBoxId);
        checkAllButton.checked = false;
      },
      changeCheckAllStatusGroup(){
        let selectList = this.$refs.pendingListTable.selectedTo;
        let renderedList = this.renderedCheckListGroup;
        if(selectList.length>=renderedList.length){
          let isEqual = false;
          for(let i=0; i<renderedList.length; i++){
            isEqual = false;
            for(let j=0; j<selectList.length; j++){
              if(renderedList[i]===selectList[j]) {j=selectList.length; isEqual=true}
            }
            if(isEqual===false){
              this.selectNoneGroup();
              break;
            }
            if(i===renderedList.length-1){
              this.selectAllGroup(true);
            }
          }
        }
        else {
          this.selectNoneGroup();
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
      onCheckStatusChangeGroup(isChecked){
        if(isChecked){
          this.changeCheckAllStatusGroup();
        }
        else {
          this.selectNoneGroup();
        }
      },
      disableSelect(){

        let context = document.getElementsByClassName("vs__selected");

        for(let i=0;i<context.length;i++){
          let span_text = context[i].textContent.trim();
          let btn = context[i].getElementsByTagName("button")[0];
          btn.removeAttribute("hidden");
          if(span_text===this.selectedDeviceName){
            btn.setAttribute("hidden", true);
            break;
          }
        }
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
      //getting all device category options
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
      //getting all site options
      getSiteData() {
        getApiManagerError().post(`${apiBaseUrl}/site-management/field/get-all`, {
          type: 'with_parent'
        }).then((response) => {
          let message = response.data.message;
          let data = response.data.data;
          switch (message) {
            case responseMessages['ok']:
              this.siteData = data;
              break;
          }
        });
      },
      getModeData() {
        getApiManagerError().post(`${apiBaseUrl}/device-management/device-config/work-mode/get-all`, {
          type: 'with_parent'
        }).then((response) => {
          let message = response.data.message;
          let data = response.data.data;
          switch (message) {
            case responseMessages['ok']:
              this.modeData = data;
              break;
          }
        });
      },
      getConfigDeviceData(deviceId = 0) {
        getApiManagerError().post(`${apiBaseUrl}/device-management/device-config/config/get-all`, {
          deviceId: deviceId
        }).then((response) => {
          let message = response.data.message;
          let data = response.data.data;
          switch (message) {
            case responseMessages['ok']:
              this.fromConfigDeviceData = data;
              break;
          }
        });
      },
      getManualDeviceData() {
        getApiManagerError().post(`${apiBaseUrl}/device-management/device-config/manual-device/get-all`).then((response) => {
          let message = response.data.message;
          let data = response.data.data;
          switch (message) {
            case responseMessages['ok']:
              let options = [];
              options = data.map(opt => ({
                label: opt.device ? opt.device.deviceName : "Unknown",
                value: opt.manualDeviceId
              }));
              this.manualDeviceOptions = options;
              break;
          }
        });
      },
      getJudgeDeviceData() {
        getApiManagerError().post(`${apiBaseUrl}/device-management/device-config/judge-device/get-all`).then((response) => {
          let message = response.data.message;
          let data = response.data.data;
          switch (message) {
            case responseMessages['ok']:
              let options = [];
              options = data.map(opt => ({
                label: opt.device ? opt.device.deviceName : "Unknown",
                value: opt.judgeDeviceId
              }));
              this.judgeDeviceOptions = options;
              break;
          }
        });
      },

      getModeValueFromId(modeId) {
        for (let item of this.modeData) {
          if (item.modeId === modeId)
            return item.modeName;
        }
        return false;
      },



      onExportButton() {
        // this.fileSelection = [];
        // this.$refs['model-export'].show();
        let checkedAll = this.$refs.configListTable.checkedAllStatus;
        let checkedIds = this.$refs.configListTable.selectedTo;
        this.params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': this.configFilter,
          'idList': checkedIds.join()
        };
        this.link = `device-management/device-table/device/field`;
        this.name = 'device-config';
        this.isModalVisible = true;
      },
      hideModal(modal) {
        this.$refs[modal].hide();
      },

      onExport() {
        this.$v.fileSelection.$touch();
        if (this.$v.fileSelection.$invalid) {
          return;
        }
        let checkedAll = this.$refs.configListTable.checkedAllStatus;
        let checkedIds = this.$refs.configListTable.selectedTo;
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': this.configFilter,
          'idList': checkedIds.join()
        };
        let link = `device-management/device-table/device/field`;
        if(this.fileSelection !== null){
        downLoadFileFromServer(link, params, 'device-config', this.fileSelection);
        this.hideModal('model-export')
        }
      },
      onPrintButton() {
        let checkedAll = this.$refs.configListTable.checkedAllStatus;
        let checkedIds = this.$refs.configListTable.selectedTo;
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': this.configFilter,
          'idList': checkedIds.join()
        };
        let link = `device-management/device-table/device/field`;
        printFileFromServer(link, params);
      },
      changeSwitchStatus(status) {
        this.switchStatus = status;
      },
      onConfigSearchButton() {
        this.$refs.configListTable.refresh();
      },
      onConfigResetButton() {
        this.configFilter = {
          deviceName: '',
          categoryId: null,
          fieldId: null
        };
      },
      onNodeSelected(node) {
        this.selectedFieldId = node.data.fieldId;
        this.getDeviceEmptyField();
        this.getDeviceByField(node.data.fieldId);
      },
      getDeviceByField(fieldId) {
        getApiManagerError().post(`${apiBaseUrl}/device-management/device-table/device/get-by-field`, {
          fieldId: fieldId, categoryId: null
        }).then((response) => {
          let message = response.data.message;
          let data = response.data.data;
          switch (message) {
            case responseMessages['ok']:
              this.devicesPerFieldData = data;
              break;
          }
        });
      },
      getDeviceEmptyField() {
        getApiManager().post(`${apiBaseUrl}/device-management/device-table/device/get-empty-field`, {
          categoryId: null
        }).then((response) => {
          let message = response.data.message;
          let data = response.data.data;
          switch (message) {
            case responseMessages['ok']:
              this.availableDevicesData = data;
              break;
          }
        });
      },

      updateItemStatus(statusValue) {
        let templateId = this.configForm.configId;
        if (templateId === 0)
          return false;
        getApiManager()
          .post(`${apiBaseUrl}/device-management/device-config/config/update`, {
            configId: templateId,
            status: statusValue
          })
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`device-management.document-template.status-updated-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                if (this.configForm.configId > 0)
                  this.configForm.status = statusValue;

                this.$refs.pendingListTable.reload();
                break;
              case responseMessages['device-online']: // okay
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-management.document-template.device-online`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['has-devices']: // okay
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-management.document-template.has-devices`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['device-not-field']: // okay
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-management.document-template.device-not-field`), {
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

      onSaveDeviceToField() {
        let options = this.$refs.fieldSelectList.options.selectedItems;
        let updatedDevice = [];
        let selectedDeviceIds = [];
        if (options.length > 0) {
          options.forEach(opt => {
            updatedDevice.push({
              deviceId: opt.id,
              fieldId: this.selectedFieldId
            });
            selectedDeviceIds.push(opt.id);
          });
        }
        this.appliedItems.forEach(item => {
          if (!selectedDeviceIds.includes(item.id))
            updatedDevice.push({
              deviceId: item.id,
              fieldId: null
            })
        });
        getApiManager().post(`${apiBaseUrl}/device-management/device-table/device/field-modify`, {
          deviceList: updatedDevice
        }).then((response) => {
          let message = response.data.message;
          let result = response.data.data;
          switch (message) {
            case responseMessages['ok']:
              this.$notify('success', this.$t('permission-management.permission-control.success'), this.$t(`device-config.field-config.updated-successful`), {
                duration: 3000,
                permanent: false
              });
              this.$refs.fieldSelectList.resetFilterOption();
              break;
          }
        });

      },
      transformConfigTable(response) {
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
          temp.deviceCategoryName = temp.archive ? temp.category.categoryName : '';
          temp.siteNameWithParent = getSiteFullName(temp.field);
          transformed.data.push(temp);
        }
        return transformed
      },
      configListTableHttpFetch(apiUrl, httpOptions) {
        this.renderedCheckList =[];
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.configListTableItems.perPage,
          sort: httpOptions.params.sort,
          filter: this.configFilter
        });
      },
      onConfigTablePaginationData(paginationData) {
        this.$refs.configListTablePagination.setPaginationData(paginationData);
        this.changeCheckAllStatus();
      },
      onConfigTableChangePage(page) {
        this.$refs.configListTable.changePage(page);
        this.changeCheckAllStatus();
      },

      ///////////////////////////////////////////
      /////   setting device with config ////////
      ///////////////////////////////////////////

      onRefreshItem(data, index) {
        this.$refs.pendingListTable.reload();
      },
      onPendingSearchButton() {
        this.$refs.pendingListTable.refresh();
      },
      onPendingResetButton() {
        this.pendingFilter = {
          deviceName: '',
          categoryId: null,
          fieldId: null,
          mode:null,
        };
      },
      onAction(value, data = null) {
        switch (value) {
          case 'list':
            this.pageStatus = 'list';
            break;
          case 'show':
            this.initializeConfigData(data);
            this.pageStatus = 'show';
            break;
          case 'edit':
            this.initializeConfigData(data);
            this.pageStatus = 'edit';
            break;
          case 'activate':
            if(this.pageStatus==='list') {
              this.initializeConfigData(data);
            }
            else{
              this.initializeConfigData(data, false);
            }
            //this.updateItemStatus('1000000701');
            this.$refs['modal-active'].show();
            break;
          case 'inactivate':
            if(this.pageStatus==='list') {
              this.initializeConfigData(data);
            }
            else{
              this.initializeConfigData(data, false);
            }
            //this.updateItemStatus('1000000702');
            this.$refs['modal-inactive'].show();
            break;
        }
      },
      initializeConfigData(rowData,  isUpdated = true) {
        getApiManagerError()
          .post(`${apiBaseUrl}/device-management/device-config/config/get-by-id`, {
            'configId': rowData.configId
          })
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']:
                this.configForm = {
                  configId: data.configId,
                  modeId: data.modeId,
                  atrSwitch: data.atrSwitch,
                  manualSwitch: data.manualSwitch,
                  manRemoteGender: data.manRemoteGender,
                  womanRemoteGender: data.womanRemoteGender,
                  manManualGender: data.manManualGender,
                  womanManualGender: data.womanManualGender,
                  manDeviceGender: data.manDeviceGender,
                  womanDeviceGender: data.womanDeviceGender,
                  status: data.status,
                  deviceId: data.deviceId,
                  judgeDeviceIdList: [],
                  manualDeviceIdList: [],
                  fromDeviceIdList: [],
                  judgeDeviceId: [],
                  manualDeviceId: [],
                  fromDeviceId: []
                };

                if(isUpdated===true) {
                  this.selectedDeviceName = null;
                  this.selectedDeviceName = rowData.deviceName;
                  let isDeviceId = false;
                  this.selectedDeviceData = {
                    fieldName: data.device.field ? data.device.field.fieldDesignation : '',
                    deviceName: data.device.deviceName,
                    category: data.device.category.categoryName
                  };
                  this.getConfigDeviceData(data.deviceId);
                  //console.log(this.configForm.modeId);


                  rowData.fromConfigIdList.forEach(item => {
                    if (item.device != null) {
                      if (item.device.deviceId === rowData.deviceId) {
                        this.configForm.fromDeviceId.push({
                          value: item.device.deviceId,
                          label: item.device.deviceName,
                        })
                        isDeviceId = true;
                      } else {
                        this.configForm.fromDeviceId.push({
                          value: item.device.deviceId,
                          label: item.device.deviceName
                        })
                      }
                    }
                  });
                  if (!isDeviceId) {
                    this.configForm.fromDeviceId.push({
                      value: rowData.deviceId,
                      label: rowData.deviceName
                    })
                  }
                  data.judgeGroupList.forEach(item => {
                    if (item.judgeDevice != null)
                      this.configForm.judgeDeviceId.push({
                        value: item.judgeDevice.deviceId,
                        label: item.judgeDevice.deviceName
                      })
                  });
                  data.manualGroupList.forEach(item => {
                    if (item.manualDevice != null)
                      this.configForm.manualDeviceId.push({
                        value: item.manualDevice.deviceId,
                        label: item.manualDevice.deviceName
                      })
                  });
                }
                break;

            }
          })
          .catch((error) => {
          });




      },
      onSaveDeviceConfig() {
          if (this.configForm.manualDeviceId.length === 0) {
            if(this.configForm.modeId === 2 || this.configForm.modeId === 4) {
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-config.required-manual-device-select`), {
                duration: 3000,
                permanent: false
              });
              return;
            }

          }
          if (this.configForm.judgeDeviceId.length === 0) {
            if(this.configForm.modeId === 3 || this.configForm.modeId === 4) {
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`device-config.required-judge-device-select`), {
                duration: 3000,
                permanent: false
              });
              return;
            }

          }

        this.configForm.manualDeviceIdList = [];
        this.configForm.judgeDeviceIdList = [];
        this.configForm.fromDeviceIdList = [];
        this.configForm.manualDeviceId.forEach(item => {
          this.configForm.manualDeviceIdList.push(item.value);
        });
        this.configForm.judgeDeviceId.forEach(item => {
          this.configForm.judgeDeviceIdList.push(item.value);
        });
        this.configForm.fromDeviceId.forEach(item => {
          if(item.label!==this.selectedDeviceName) {
            this.configForm.fromDeviceIdList.push(item.value);
          }
        });
        this.isLoading = true;
        getApiManager().post(`${apiBaseUrl}/device-management/device-config/config/modify`, this.configForm).then((response) => {
          let message = response.data.message;
          let result = response.data.data;
          switch (message) {
            case responseMessages['ok']:
              this.$notify('success', this.$t('permission-management.permission-control.success'), this.$t(`device-config.maintenance-config.updated-successful`), {
                duration: 3000,
                permanent: false
              });
              this.$refs.pendingListTable.refresh();
              let that = this;
              this.isLoading = false;
              //this.$refs.pendingListTable.props.rowData = this.configForm;
              // setTimeout(function(){
              //   this.isLoading = false;
              // },4000);
              this.pageStatus = 'list';

              break;
          }
          this.isLoading = false;
        })
          .catch((error) => {
            this.isLoading = false;
          });
        this.isLoading = false;

      },
      onDeleteDeviceConfig() {
        return;
        getApiManager().post(`${apiBaseUrl}/device-management/device-config/config/delete`, this.configForm).then((response) => {
          let message = response.data.message;
          let result = response.data.data;
          switch (message) {
            case responseMessages['ok']:
              this.$notify('success', this.$t('permission-management.permission-control.success'), this.$t(`device-config.maintenance-config.updated-successful`), {
                duration: 3000,
                permanent: false
              });
              this.$refs.pendingListTable.refresh();
              this.pageStatus = 'list';
              break;
          }
        });
      },
      //table showing options
      transformPendingTable(response) {
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
          temp.deviceSerialNumber = temp.device ? temp.device.deviceSerial : '';
          temp.deviceName = temp.device ? temp.device.deviceName : '';
          temp.siteNameWithParent = getSiteFullName(temp.device ? temp.device.field : null);
         // temp.fromConfig = temp.fromConfigIdList.length > 0 ? temp.fromConfigDeviceName : '';
          transformed.data.push(temp);
        }
        return transformed
      },
      pendingListTableHttpFetch(apiUrl, httpOptions) {
        return getApiManagerError().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.pendingListTableItems.perPage,
          sort: httpOptions.params.sort,
          filter: this.pendingFilter
        });
      },
      onPendingListTablePaginationData(paginationData) {
        this.$refs.pendingListTablePagination.setPaginationData(paginationData);
      },
      onPendingListTableChangePage(page) {
        this.$refs.pendingListTable.changePage(page);
      },
    },
    watch: {
      'configListTableItems.perPage': function (newVal) {
        this.$refs.configListTable.refresh();
      },
      'pendingListTableItems.perPage': function (newVal) {
        this.$refs.pendingListTable.refresh();
      },

      'configForm.fromDeviceId': function (newVal) {
        console.log(newVal);
        let that = this;
        setTimeout(function(){
          that.disableSelect();
        },300);
      },
      'configForm.manualSwitch': function (newVal) {
        console.log(newVal);
       if(newVal === '1000000602'){
         this.configForm.manDeviceGender = null;
         this.configForm.womanDeviceGender = null;

       }
      },

      // 'configForm.modeId': function (newVal) {
      //
      // },
      categoryData(newVal, oldVal) { // maybe called when the org data is loaded from server

        let options = [];
        if (newVal.length === 0) {
          options.push({
            value: null,
            html: `${this.$t('system-setting.none')}`
          });
        }
        else {
          options = newVal.map(site => ({
            text: site.categoryName,
            value: site.categoryId
          }));
        }
        this.deviceCategoryOptions = JSON.parse(JSON.stringify(options));
        this.deviceCategoryOptions.push({value: null, text: `全部`});
        this.$refs.fieldSelectList.setFilterOptions(this.deviceCategoryOptions);
      },
      siteData(newVal, oldVal) { // maybe called when the org data is loaded from server
        let getLevel = (org) => {

          let getParent = (org) => {
            for (let i = 0; i < newVal.length; i++) {
              if (newVal[i].fieldId === org.parentFieldId) {
                return newVal[i];
              }
            }
            return null;
          };

          let stepValue = org;
          let level = 0;
          while (getParent(stepValue) !== null) {
            stepValue = getParent(stepValue);
            level++;
          }

          return level;

        };

        let generateSpace = (count) => {
          let string = '';
          while (count--) {
            string += '&nbsp;&nbsp;&nbsp;&nbsp;';
          }
          return string;
        };
        this.siteSelectOptions = [];
        this.siteSelectOptions = newVal.map(org => ({
          value: org.fieldId,
          html: `${generateSpace(getLevel(org))}${org.fieldDesignation}`
        }));
        this.siteSelectOptions.push({
          value: null,
          html: `${this.$t('permission-management.all')}`
        });
        let nest = (newVal, id = 0, depth = 1) =>
          newVal
            .filter(item => item.parentFieldId == id)
            .map(item => ({
              data: {fieldId: item.fieldId},
              children: nest(newVal, item.fieldId, depth + 1),
              id: id++,
              state: {expanded: true},
              text: item.fieldDesignation
            }));
        this.siteTreeData = nest(newVal);
        this.isLoadCompleted = true;
      },
      modeData(newVal, oldVal) { // maybe called when the org data is loaded from server
        let options = [];
        options = newVal.map(site => ({
          text: this.modeDictionaryData[site.modeName],
          value: site.modeId
        }));
        this.modeSelectData = options;
      },
      availableDevicesData(newVal) {
        let options = [];
        options = newVal.map(opt => ({
          id: opt.deviceId,
          name: opt.deviceName,
          category: opt.category.categoryId
        }));
        this.$refs.fieldSelectList.setAvailableItem(options);
      },
      devicesPerFieldData(newVal) {
        let options = [];
        options = newVal.map(opt => ({
          id: opt.deviceId,
          name: opt.deviceName,
          category: opt.category.categoryId
        }));
        this.appliedItems = JSON.parse(JSON.stringify(options));
        this.$refs.fieldSelectList.setAppliedItem(options);
      },
      fromConfigDeviceData(newVal, oldVal) { // maybe called when the org data is loaded from server
        let options = [];
        newVal.forEach((opt) => {
          if (opt.device !== null) {
            options.push({
              label: opt.device.deviceName,
              value: opt.device.deviceId,
              removable:false
            })
          }
        });
        this.fromConfigDeviceSelectOptions = options;
      },
      treeFilter: function (newVal, oldVal) {
        //this.selectedFieldId = 0;
      }
    }
  }
</script>
