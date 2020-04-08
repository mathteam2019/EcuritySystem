<style lang="scss">
  .vs__selected-options {
    margin-left: 1px;
    margin-bottom: 1px;
  }
  .system-setting {
    .v-select.v-select-custom-style {
      & > div {
        border-radius: 0.3rem !important;

        & > div {
          border-radius: 0.3rem !important;
        }
      }

    }
.class-form-label {
  margin-bottom: 1px;
}
  }
  .pointer {cursor: pointer;}
</style>
<template>
  <div class="system-setting">
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb/>
        </b-colxx>
      </b-row>
    </div>

    <b-tabs v-show="!isLoading" nav-class="ml-2" :no-fade="true">
      <b-tab :title="$t('system-setting.parameter-setting.platform-parameter')">

        <div class="section pt-0 mx-3">
          <b-tabs class="sub-tabs" v-model="tabIndex" card>
            <b-tab :title="$t('menu.personal-inspection')" style="height: auto;">
              <div>
                <b-row>
                  <b-col cols="1">
                    <label class="font-weight-bold">{{$t('system-setting.parameter-setting.scan')}}</label>
                  </b-col>
                  <b-col cols="2">
                    <b-form-group :label="$t('system-setting.parameter-setting.atr-suspect-box-color')" class="pointer">
                      <colorpicker :color="platFormData.scanRecogniseColour"
                                   v-model="platFormData.scanRecogniseColour"
                                   :change="onChange()"
                                   style="margin-bottom: 0 !important;"/>
                    </b-form-group>
                  </b-col>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.work-timeout-reminder')">
                      <b-form-input type="number" v-model="platFormData.scanOverTime"
                                    :state="!$v.platFormData.scanOverTime.$dirty ? null : !$v.platFormData.scanOverTime.$invalid"/>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col cols="1">
                    <label class="font-weight-bold">{{$t('system-setting.parameter-setting.judgement')}}</label>
                  </b-col>
                  <b-col cols="2">
                    <b-form-group :label="$t('system-setting.parameter-setting.dispatch-timeout')">
                      <b-form-input type="number" v-model="platFormData.judgeAssignTime"
                                    :state="!$v.platFormData.judgeAssignTime.$dirty ? null : !$v.platFormData.judgeAssignTime.$invalid"/>
                    </b-form-group>
                  </b-col>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.processing-timeout-period')">
                      <b-form-input type="number" v-model="platFormData.judgeProcessingTime"
                                    :state="!$v.platFormData.judgeProcessingTime.$dirty ? null : !$v.platFormData.judgeProcessingTime.$invalid"/>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.judge-timeout-reminder')">
                      <b-form-input type="number" v-model="platFormData.judgeScanOvertime"
                                    :state="!$v.platFormData.judgeScanOvertime.$dirty ? null : !$v.platFormData.judgeScanOvertime.$invalid"/>
                    </b-form-group>
                  </b-col>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.judgement-frame-color')">
                      <colorpicker :color="platFormData.judgeRecogniseColour" v-model="platFormData.judgeRecogniseColour" :change="onChange()"
                                   style="margin-bottom: 0rem !important;"/>
                    </b-form-group>
                  </b-col>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.hand-timeout-reminder')">
                      <b-form-input type="number" v-model="platFormData.handOverTime"
                                    :state="!$v.platFormData.handOverTime.$dirty ? null : !$v.platFormData.handOverTime.$invalid"/>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col cols="1">
                    <label class="font-weight-bold">{{$t('system-setting.parameter-setting.history-task')}}</label>
                  </b-col>
                  <b-col cols="2">
                    <b-form-group :label="$t('system-setting.parameter-setting.data-storage')">
                      <v-select v-model="platFormData.historyDataStorageSelect" :options="dataStorageOptions"
                                :state="!$v.platFormData.historyDataStorageSelect.$invalid"
                                class="v-select-custom-style" multiple :searchable="false" :dir="direction"/>
                    </b-form-group>
                  </b-col>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.data-output')">
                      <v-select v-model="platFormData.historyDataExportSelect" :options="dataStorageOptions"
                                :state="!$v.platFormData.historyDataExportSelect.$invalid"
                                class="v-select-custom-style" multiple :searchable="false" :dir="direction"/>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col>
                    <label
                      class="font-weight-bold">{{$t('system-setting.parameter-setting.hand-process-history')}}</label>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.show-deleted-suspected-box')">
                      <b-form-select v-model="platFormData.displayDeleteSuspicion"
                                     :options="displayDeleteSuspicionOptions" plain/>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col>
                    <label
                      class="font-weight-bold">{{$t('system-setting.parameter-setting.hand-process-history')}}</label>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col cols="2" offset="1">
                    <b-form-group class="mb-0"
                                  :label="$t('system-setting.parameter-setting.deleted-suspected-box-color')">
                      <colorpicker :disablePicker="platFormData.displayDeleteSuspicion === '1000000602'" :color="platFormData.displayDeleteSuspicionColour" v-model="platFormData.displayDeleteSuspicionColour" @input="onInput()" :change="onChange()"/>
                    </b-form-group>
                  </b-col>

                  <b-col cols="12" class="d-flex justify-content-end align-self-end">
                    <b-button v-if="tabIndex === 0" size="sm" variant="info default" class="mr-3" @click="savePlatFormData()"
                              :disabled="checkPermItem('platform_check_modify')">
                      <i class="icofont-save"/>
                      {{$t('permission-management.permission-control.save')}}
                    </b-button>
                  </b-col>
                </b-row>
              </div>
            </b-tab>
            <b-tab :title="$t('permission-management.other')" style="height: auto;">
              <div>
                <b-row class="mb-3">
                  <b-col cols="7">
                    <b-row>
                      <b-col cols="auto" class="d-flex align-items-center">
                        <span class="font-weight-bold">
                          {{$t('menu.permission-management')}}
                        </span>
                      </b-col>
                      <b-col>
                        <hr/>
                      </b-col>
                    </b-row>
                  </b-col>
                </b-row>

                <b-row>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('permission-management.password')">
                      <b-form-input type="password" v-model="platFormOtherData.initialPassword"
                                    :state="!$v.platFormOtherData.initialPassword.$dirty ? null : !$v.platFormOtherData.initialPassword.$invalid"/>
                    </b-form-group>
                  </b-col>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.login-fail-count')">
                      <!--<label>{{platFormOtherData.loginNumber}}</label>-->
                      <b-form-input type="number" v-model="platFormOtherData.loginNumber"
                                    :state="!$v.platFormOtherData.loginNumber.$dirty ? null : !$v.platFormOtherData.loginNumber.$invalid"/>
                    </b-form-group>
                  </b-col>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.operating-time-limit')">
                      <!--<label>{{platFormOtherData.loginNumber}}</label>-->
                      <b-form-input type="number" v-model="platFormOtherData.operatingTimeLimit"
                                    :state="!$v.platFormOtherData.operatingTimeLimit.$dirty ? null : !$v.platFormOtherData.operatingTimeLimit.$invalid"/>
                    </b-form-group>
                  </b-col>
                </b-row>

                <b-row class="mb-3 mt-2">
                  <b-col cols="7">
                    <b-row>
                      <b-col cols="auto" class="d-flex align-items-center">
                        <span class="font-weight-bold">
                          {{$t('menu.log-management')}}
                        </span>
                      </b-col>
                      <b-col>
                        <hr/>
                      </b-col>
                    </b-row>
                  </b-col>
                </b-row>

                <b-row>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.log-export-number')">
                      <b-form-input type="number" v-model="platFormOtherData.logMaxNumber"
                                    :state="!$v.platFormOtherData.logMaxNumber.$dirty ? null : !$v.platFormOtherData.logMaxNumber.$invalid"/>
                    </b-form-group>
                  </b-col>
                </b-row>

                <b-row class="mb-3 mt-2">
                  <b-col cols="7">
                    <b-row>
                      <b-col cols="auto" class="d-flex align-items-center">
                        <span class="font-weight-bold">
                          {{$t('system-setting.parameter-setting.business-statistics')}}
                        </span>
                      </b-col>
                      <b-col>
                        <hr/>
                      </b-col>
                    </b-row>
                  </b-col>
                </b-row>

                <b-row>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.security-instrument-flow-setting')">
                      <b-form-select v-model="platFormOtherData.deviceTrafficSettings" :options="levelOptions"
                                     plain/>
                      <div class="invalid-feedback d-block">
                        {{ (submitted && !$v.platFormOtherData.deviceTrafficSettings.required) ?
                        $t('device-management.device-classify-item.field-is-mandatory') : " " }}
                      </div>

                    </b-form-group>
                  </b-col>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.security-instrument-flow-high')">
                      <b-form-input type="number" v-model="platFormOtherData.deviceTrafficHigh"
                                    :state="!$v.platFormOtherData.deviceTrafficHigh.$dirty ? null : !$v.platFormOtherData.deviceTrafficHigh.$invalid"/>
                      <div class="invalid-feedback d-block">
                        {{ (submitted && (!$v.platFormOtherData.deviceTrafficHigh.minValue ||
                        !$v.platFormOtherData.deviceTrafficHigh.maxValue)) ?
                        $t('system-setting.parameter-setting.field-value-range-0-400') : " " }}
                      </div>
                    </b-form-group>
                  </b-col>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.security-instrument-flow-middle')">
                      <b-form-input type="number" v-model="platFormOtherData.deviceTrafficMiddle"
                                    :state="!$v.platFormOtherData.deviceTrafficMiddle.$dirty ? null : !$v.platFormOtherData.deviceTrafficMiddle.$invalid"/>
                      <div class="invalid-feedback d-block">
                        {{ (submitted && (!$v.platFormOtherData.deviceTrafficMiddle.minValue ||
                        !$v.platFormOtherData.deviceTrafficMiddle.maxValue)) ?
                        $t('system-setting.parameter-setting.field-value-range-0-400') : " " }}
                      </div>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row class="mb-3 mt-2">
                  <b-col cols="7">
                    <b-row>
                      <b-col cols="auto" class="d-flex align-items-center">
                        <span class="font-weight-bold">
                          {{$t('permission-management.other')}}
                        </span>
                      </b-col>
                      <b-col>
                        <hr/>
                      </b-col>
                    </b-row>
                  </b-col>
                </b-row>

                <b-row>
                  <b-col>
                    <label class="font-weight-bold">{{$t('system-setting.parameter-setting.platform-security-instrument')}}</label>
                  </b-col>
                </b-row>

                <b-row>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.storage-check-period')">
                      <b-form-input type="number" v-model="platFormOtherData.storageDetectionCycle"
                                    :state="!$v.platFormOtherData.storageDetectionCycle.$dirty ? null : !$v.platFormOtherData.storageDetectionCycle.$invalid"/>
                    </b-form-group>
                  </b-col>

                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.storage-warning-size')">
                      <b-form-input type="number" v-model="platFormOtherData.storageAlarm"
                                    :state="!$v.platFormOtherData.storageAlarm.$dirty ? null : !$v.platFormOtherData.storageAlarm.$invalid"/>
                    </b-form-group>
                  </b-col>

                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.history-save-period')">
                      <b-form-input type="number" v-model="platFormOtherData.historyDataCycle"
                                    :state="!$v.platFormOtherData.historyDataCycle.$dirty ? null : !$v.platFormOtherData.historyDataCycle.$invalid"/>
                    </b-form-group>
                  </b-col>

                  <b-col cols="12" class="d-flex justify-content-end align-self-end">
                    <b-button v-if="tabIndex === 1" size="sm" variant="info default" class="mr-3" @click="savePlatFormData()"
                              :disabled="checkPermItem('platform_other_modify')">
                      <i class="icofont-save"/>
                      {{$t('permission-management.permission-control.save')}}
                    </b-button>
                  </b-col>
                </b-row>

              </div>
            </b-tab>
          </b-tabs>
        </div>

<!--        <div class="text-right mr-3 mt-3" style="margin-top: 0.5rem;">-->
<!--          <b-button v-if="tabIndex === 0" size="sm" variant="info default" class="mr-3" @click="savePlatFormData()"-->
<!--                    :disabled="checkPermItem('platform_check_modify')">-->
<!--            <i class="icofont-save"/>-->
<!--            {{$t('permission-management.permission-control.save')}}-->
<!--          </b-button>-->
<!--          <b-button v-if="tabIndex === 1" size="sm" variant="info default" class="mr-3" @click="savePlatFormData()"-->
<!--                    :disabled="checkPermItem('platform_other_modify')">-->
<!--            <i class="icofont-save"/>-->
<!--            {{$t('permission-management.permission-control.save')}}-->
<!--          </b-button>-->
<!--        </div>-->

      </b-tab>

      <b-tab :title="$t('system-setting.parameter-setting.security-instrument')">
        <b-row v-show="pageStatus === 'table'" class="h-100 ">
          <b-col cols="12 d-flex flex-column">
            <b-row class="pt-2">
              <b-col cols="6">
                <b-row>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.device')">
                      <b-form-input v-model="filter.deviceName"/>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('system-setting.status')">
                      <b-form-select v-model="filter.status" :options="stateOptions" plain/>
                    </b-form-group>
                  </b-col>
                  <b-col>
                  </b-col>

                </b-row>
              </b-col>
              <b-col cols="6" class="d-flex justify-content-end align-items-center">
                <div>
                  <b-button size="sm" class="ml-2" variant="info default" @click="onSearchButton()">
                    <i class="icofont-search-1"/>&nbsp;{{ $t('permission-management.search') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="info default" @click="onResetButton()">
                    <i class="icofont-ui-reply"/>&nbsp;{{$t('permission-management.reset') }}
                  </b-button>
                </div>
              </b-col>
            </b-row>
            <b-row class="flex-grow-1">
              <b-col cols="12">
                <div class="table-wrapper table-responsive">
                  <vuetable
                    ref="vuetable"
                    :fields="vuetableItems.fields"
                    :per-page="vuetableItems.perPage"
                    :api-url="vuetableItems.apiUrl"
                    :http-fetch="tableHttpFetch"
                    pagination-path="pagination"
                    class="table-striped"
                    @vuetable:pagination-data="onTablePaginationData"
                  >
                    <template slot="deviceNumber" slot-scope="props">
                      <span class="cursor-p text-primary" @click="onAction('show', props.rowData)">{{ props.rowData.deviceNumber }}</span>
                    </template>
                    <template slot="operating" slot-scope="props">
                      <div>

                        <b-button
                          size="sm" :disabled="checkPermItem('scan_param_modify')"
                          variant="info default btn-square"
                          @click="onAction('modify', props.rowData)">
                          <i class="icofont-edit"/>
                        </b-button>

                        <b-button
                          v-if="props.rowData.status==='1000000702'"
                          size="sm" @click="onAction('activate',props.rowData)"
                          variant="success default btn-square" :disabled="checkPermItem('scan_param_update_status')"
                        >
                          <i class="icofont-check-circled"/>
                        </b-button>
                        <b-button @click="onAction('inactivate',props.rowData)"
                                  v-if="props.rowData.status==='1000000701'"
                                  size="sm"
                                  variant="warning default btn-square" :disabled="checkPermItem('scan_param_update_status')"
                        >
                          <i class="icofont-ban"/>
                        </b-button>
                      </div>
                    </template>

                  </vuetable>
                </div>
                <div class="pagination-wrapper">
                  <vuetable-pagination-bootstrap
                    ref="pagination"
                    @vuetable-pagination:change-page="onTableChangePage"
                    :initial-per-page="vuetableItems.perPage"
                    @onUpdatePerPage="vuetableItems.perPage = Number($event)"
                  />
                </div>
              </b-col>
            </b-row>
          </b-col>
        </b-row>
        <b-row v-if="pageStatus !== 'table'" class="h-100 form-section">
          <b-col cols="10">
            <b-row>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.position')}}
                  </template>
                  <label>{{scanForm.fieldDesignation}}</label>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.device-classification')}}
                  </template>
                  <label>{{$t('statistics.evaluate-monitors.security-device')}}</label>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('device-config.maintenance-config.device')}}
                  </template>
                  <label>{{scanForm.deviceName}}</label>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col cols="6">
                <b-form-group class="mw-100">
                  <template slot="label">
                    {{$t('system-setting.parameter-setting.suitable-for')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <v-select v-model="scanForm.fromDeviceId" :options="deviceSelectOptions" class="v-select-custom-style"
                            multiple :searchable="false" :dir="direction"/>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row class="mb-2">
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('system-setting.parameter-setting.cover-correction-time')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input v-model="scanForm.airCaliWarnTime" :state="!$v.scanForm.airCaliWarnTime.$dirty ? null : !$v.scanForm.airCaliWarnTime.$invalid"/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('system-setting.standby-time')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input v-model="scanForm.standByTime" :state="!$v.scanForm.standByTime.$dirty ? null : !$v.scanForm.standByTime.$invalid"/>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row class="mb-2">
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('system-setting.parameter-setting.alarm-prompt')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-select v-model="scanForm.alarmSound" :options="yesNoOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('system-setting.parameter-setting.pass-prompt')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-select v-model="scanForm.passSound" :options="yesNoOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('system-setting.parameter-setting.pos-error-prompt')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-select v-model="scanForm.posErrorSound" :options="yesNoOptions" plain/>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row class="mb-2">
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('system-setting.parameter-setting.start-scan-remind')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-select v-model="scanForm.standSound" :options="yesNoOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('system-setting.parameter-setting.while-scan')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-select v-model="scanForm.scanSound" :options="yesNoOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('system-setting.parameter-setting.complete-scan-prompt')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-select v-model="scanForm.scanOverUseSound" :options="yesNoOptions" plain/>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row class="mb-2">
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('system-setting.parameter-setting.auxiliary-recognition')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-select v-model="scanForm.autoRecognise" :options="yesNoOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('system-setting.parameter-setting.auxiliary-recognition-level')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-select v-model="scanForm.recognitionRate" :options="bitOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('system-setting.parameter-setting.save-history-image')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-select v-model="scanForm.saveScanData" :options="yesNoOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('system-setting.parameter-setting.save-only-suspected-image')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-select v-model="scanForm.saveSuspectData" :options="yesNoOptions" plain/>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row class="mb-2">
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('system-setting.facial-blurring')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-select v-model="scanForm.facialBlurring" :options="yesNoOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('system-setting.chest-blurring')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-select v-model="scanForm.chestBlurring" :options="yesNoOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('system-setting.hip-blurring')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-select v-model="scanForm.hipBlurring" :options="yesNoOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('system-setting.groin-blurring')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-select v-model="scanForm.groinBlurring" :options="yesNoOptions" plain/>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row class="mb-2">
              <b-col cols="3">
                <b-form-group :label="$t('system-setting.parameter-setting.storage-alarm')">
                  <b-form-input type="number" v-model="scanForm.deviceStorageAlarm" :state="!$v.scanForm.deviceStorageAlarm.$dirty ? null : !$v.scanForm.deviceStorageAlarm.$invalid"/>
                </b-form-group>
              </b-col>
<!--              <b-col cols="3">-->
<!--                <b-form-group :label="$t('system-setting.parameter-setting.storage-warning-size-percentage')">-->
<!--                  <b-form-input type="number" v-model="scanForm.deviceStorageAlarmPercent" :state="!$v.scanForm.deviceStorageAlarmPercent.$dirty ? null : !$v.scanForm.deviceStorageAlarmPercent.$invalid"/>-->
<!--                </b-form-group>-->
<!--              </b-col>-->
            </b-row>

          </b-col>
          <b-col>
            <div v-if="getLocale()==='zh'" class="position-absolute" style="right: 8%;bottom: 7%">
              <img v-if="scanForm.status === '1000000702'" src="../../../assets/img/no_active_stamp.png">
              <img v-else-if="scanForm.status === '1000000701'" src="../../../assets/img/active_stamp.png">
            </div>
            <div v-if="getLocale()==='en'" class="position-absolute" style="right: 8%;bottom: 7%">
              <img v-if="scanForm.status === '1000000702'" src="../../../assets/img/no_active_stamp_en.png" class="img-rotate">
              <img v-else-if="scanForm.status === '1000000701'" src="../../../assets/img/active_stamp_en.png" class="img-rotate">
            </div>
          </b-col>
          <b-col cols="12" class="d-flex justify-content-end align-self-end">
            <div>
              <b-button @click="onSaveScanFormData()" variant="success default"
                        :disabled="checkPermItem('scan_param_modify')"
                        v-if="pageStatus==='modify'"
                        size="sm"><i class="icofont-save"/>
                {{ $t('permission-management.save-button') }}
              </b-button>
              <b-button v-if="scanForm.status === '1000000701'"
                        @click="onAction('inactivate',scanForm)" size="sm"
                        variant="warning default" :disabled="checkPermItem('scan_param_update_status')">
                <i class="icofont-ban"/> {{$t('system-setting.status-inactive')}}
              </b-button>
              <b-button v-if="scanForm.status === '1000000702'"
                        @click="onAction('activate',scanForm)" size="sm"
                        :disabled="checkPermItem('scan_param_update_status')"
                        variant="success default">
                <i class="icofont-check-circled"/> {{$t('system-setting.status-active')}}
              </b-button>
              <b-button @click="onAction('back')" variant="info default" size="sm"><i
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
      {{$t('device-management.device-list.make-inactive-prompt')}}
      <template slot="modal-footer">
        <b-button variant="primary" @click="updateItemStatus('1000000702')" class="mr-1">
          {{$t('system-setting.ok')}}
        </b-button>
        <b-button variant="danger" @click="hideModal('modal-inactive')">{{$t('system-setting.cancel')}}
        </b-button>
      </template>
    </b-modal>
    <b-modal centered id="modal-active" ref="modal-active" :title="$t('system-setting.prompt')">
      {{$t('device-management.device-list.make-active-prompt')}}
      <template slot="modal-footer">
        <b-button variant="primary" @click="updateItemStatus('1000000701')" class="mr-1">
          {{$t('system-setting.ok')}}
        </b-button>
        <b-button variant="danger" @click="hideModal('modal-active')">{{$t('system-setting.cancel')}}
        </b-button>
      </template>
    </b-modal>
  </div>
</template>

<style lang="scss">
  .form-control:disabled, .form-control[readonly] {
    background-color: white;
    opacity: 1;
  }
  .col-form-label {
    margin-bottom: 1px;
  }
  .vc-chrome {
    position: absolute;
    top: calc(2rem + 4px);
    right: 0;
    z-index: 9;
  }

  .current-color {
    display: inline-block;
    width: 1rem;
    height: 1rem;
    background-color: #000;
    cursor: pointer;
  }
  .img-rotate{
    -ms-transform: rotate(-15deg); /* IE 9 */
    -webkit-transform: rotate(-15deg); /* Safari 3-8 */
    transform: rotate(-15deg);
  }

</style>

<script>
  import Vuetable from '../../../components/Vuetable2/Vuetable'
  import VuetablePagination from 'vuetable-2/src/components/VuetablePagination'
  import VuetablePaginationBootstrap from '../../../components/Common/VuetablePaginationBootstrap'
  import {getApiManager, isColorValid, getApiManagerError, isAccountValid, isSpaceContain, isDataCodeValid, getDateTimeWithFormat} from '../../../api';
  import {responseMessages} from '../../../constants/response-messages';
  import {apiBaseUrl, apiParamUrl} from "../../../constants/config";
  import ColorPicker from '../../../components/ColorPicker/VueColorPicker'
  import {validationMixin} from 'vuelidate';
  import {checkPermissionItem, getDirection, getLocale} from "../../../utils";
  import vSelect from 'vue-select'
  import 'vue-select/dist/vue-select.css'

  const {required, minValue, maxValue, minLength, maxLength} = require('vuelidate/lib/validators');

  let findDicTextData = (options, value, flag = true) => {
    let name = null;
    if (options == null || options.length === 0)
      return name;
    options.forEach(option => {
      if (option.value === value)
        name = flag ? option.text : option;
    });
    return name;
  };

  export default {
    components: {
      'vuetable': Vuetable,
      'vuetable-pagination': VuetablePagination,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
      'colorpicker': ColorPicker,
      'v-select': vSelect
    },
    mixins: [validationMixin],
    validations: {
      scanForm:{
        airCaliWarnTime:{
          required,
          minValue: minValue(0), maxValue: maxValue(600)
        },
        standByTime:{
          required,
          minValue: minValue(0), maxValue: maxValue(60)
        },
        deviceStorageAlarm:{
          required,
          minValue: minValue(0), maxValue: maxValue(1000)
        },

      },
      platFormData: {
        scanRecogniseColour: {
          required,
          minLength:minLength(7), maxLength:maxLength(7),
          isColorValid
        },
        scanOverTime: {
          required,
          minValue: minValue(0), maxValue: maxValue(720)
        },
        judgeAssignTime: {
          required,
          minValue: minValue(0), maxValue: maxValue(60)
        },
        judgeProcessingTime: {
          required,
          minValue: minValue(0), maxValue: maxValue(300)
        },
        judgeScanOvertime: {
          required,
          minValue: minValue(0), maxValue: maxValue(720)
        },
        handOverTime: {
          required,
          minValue: minValue(0), maxValue: maxValue(720)
        },
        judgeRecogniseColour: {
          required,
          minLength:minLength(7), maxLength:maxLength(7),
          isColorValid
        },
        displayDeleteSuspicionColour: {
          required,
          minLength:minLength(7), maxLength:maxLength(7),
          isColorValid
        },
        historyDataStorageSelect: {
          required
        },
        historyDataExportSelect: {
          required
        }
      },
      platFormOtherData: {
        initialPassword: {
          required,
          isAccountValid,isSpaceContain,
          minLength: minLength(6), maxLength: maxLength(20)
        },
        loginNumber: {
          required,
          minValue: minValue(0), maxValue: maxValue(8)
        },
        operatingTimeLimit: {
          required,
          minValue: minValue(0), maxValue: maxValue(60)
        },
        logMaxNumber: {
          required,
          minValue: minValue(0), maxValue: maxValue(10000)
        },
        historyDataCycle: {
          required,
          minValue: minValue(0), maxValue: maxValue(5000)
        },
        storageAlarm: {
          required,
          minValue: minValue(0), maxValue: maxValue(100)
        },
        storageDetectionCycle: {
          required,
          minValue: minValue(0), maxValue: maxValue(600)
        },
        deviceTrafficSettings: {
          required
        },
        deviceTrafficHigh: {
          required, isDataCodeValid,
          minValue: minValue(0), maxValue: maxValue(400)
        },
        deviceTrafficMiddle: {
          required, isDataCodeValid,
          minValue: minValue(0), maxValue: maxValue(400)
        }
      }
    },
    mounted() {
      this.getPlatFormData();
      this.getPlatFormOtherData();
      this.getScanParamsData();
      this.$refs.vuetable.$parent.transform = this.transformTable.bind(this);

    },
    watch: {
      'vuetableItems.perPage': function (newVal) {
        this.$refs.vuetable.refresh();
      },
      scanParams: function (newVal) {
        let selectOptions = [];
        newVal.forEach((scan) => {
          selectOptions.push({
            value: scan.deviceId,
            label: scan.device ? scan.device.deviceName : 'dev-' + scan.deviceId
          });
        });
        this.deviceSelectOptions = selectOptions;
      },
      'platFormData.historyDataStorageSelect': function (newVal) {
        let that = this;
        setTimeout(function(){
          that.disableSelect();
        },100);
      },
      'scanForm.fromDeviceId': function (newVal) {
        let that = this;
        setTimeout(function(){
          that.disableSelect();
        },100);
      },
      'platFormData.historyDataExportSelect': function (newVal) {
        let that = this;
        setTimeout(function(){
          that.disableSelect();
        },100);
      },
    },
    data() {
      return {
        isLoading: false,
        direction: getDirection().direction,
        tabIndex: 0,
        submitted: false,
        pageStatus: 'table',
        filter: {
          deviceName: '',
          status: null
        },
        stateOptions: [
          {value: null, text: this.$t('permission-management.all')},
          {value: '1000000701', text: this.$t('permission-management.active')},
          {value: '1000000702', text: this.$t('permission-management.inactive')}
        ],
        vuetableItems: {
          apiUrl: `${apiBaseUrl}/system-setting/scan-param/get-by-filter-and-page`,
          perPage: 10,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__sequence',
              title: this.$t('permission-management.th-no'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:deviceNumber',
              sortField: 'deviceSerial',
              title: this.$t('device-management.device-classify-item.classify-number'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'deviceName',
              title: this.$t('log-management.device-log.device'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'siteName',
              title: this.$t('system-setting.site'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:operating',
              title: this.$t('permission-management.th-org-actions'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '150px'
            },

          ]
        },
        scanParams: [],
        deviceSelectOptions: [],
        selectedDeviceId: 0,
        yesNoOptions: [
          {value: 'TRUE', text: this.$t('system-setting.parameter-setting.yes')},
          {value: 'FALSE', text: this.$t('system-setting.parameter-setting.no')},
        ],
        displayDeleteSuspicionOptions: [
          {value: '1000000601', text: this.$t('system-setting.parameter-setting.yes')},
          {value: '1000000602', text: this.$t('system-setting.parameter-setting.no')},
        ],
        bitOptions: [
          {value: -1, text: this.$t('system-setting.level-low')},
          {value: 0, text: this.$t('system-setting.level-middle')},
          {value: 1, text: this.$t('system-setting.level-high')},
        ],
        //platForm setting
        platFormData: {
          scanId: 0,
          scanRecogniseColour: null,
          scanOverTime: null,
          judgeAssignTime: null,
          judgeProcessingTime: null,
          judgeScanOvertime: null,
          judgeRecogniseColour: null,
          handOverTime: null,
          handRecogniseColour: null,
          historyDataStorageList: [],
          historyDataExportList: [],
          displayDeleteSuspicion: null,
          displayDeleteSuspicionColour: null,
          historyDataStorageSelect: [],
          historyDataExportSelect: [],
        },
        platFormOtherData: {
          id: 0,
          initialPassword: null,
          loginNumber: null,
          operatingTimeLimit:null,
          logMaxNumber: null,
          deviceTrafficSettings: 10,
          storageDetectionCycle: null,
          storageAlarm: null,
          historyDataCycle: null,
          deviceTrafficHigh: 0,
          deviceTrafficMiddle: 0,
        },
        dataStorageOptions: [
          {value: '1000002201', label: this.$t('system-setting.storage-business')},
          {value: '1000002202', label: this.$t('system-setting.storage-cartoon')},
          {value: '1000002204', label: this.$t('system-setting.storage-original')},
        ],
        levelOptions: [
          {value: 10, text: '10'},
          {value: 20, text: '20'},
          {value: 30, text: '30'},
          {value: 60, text: '60'}
        ],
        selectedDeviceName:'',
        isEmptyScan : false,
        isEmptyJudge : false,
        isEmptyDelete :false,
        scanForm: {
          fieldDesignation:null,
          category:null,
          deviceName:null,
          scanParamsId: 0,
          status:null,
          airCaliWarnTime: null,
          standByTime: null,
          alarmSound: null,
          passSound: null,
          posErrorSound: null,
          standSound: null,
          scanSound: null,
          scanOverUseSound: null,
          autoRecognise: null,
          recognitionRate: null,
          saveScanData: null,
          saveSuspectData: null,
          facialBlurring: null,
          chestBlurring: null,
          hipBlurring: null,
          groinBlurring: null,
          deviceId: null,
          fromDeviceId: [],
          fromDeviceIdList: [],
          deviceStorageAlarmPercent: null,
          deviceStorageAlarm: null
        },
      }
    },
    methods: {
      getEvent(e){

      },
      onChange() {
        //console.log(this.platFormData.scanRecogniseColour, this.platFormData.judgeRecogniseColour, this.platFormData.displayDeleteSuspicionColour)
        // this.isEmptyScan = this.platFormData.scanRecogniseColour === '#';
        // this.isEmptyJudge = this.platFormData.judgeRecogniseColour === '#';
        // this.isEmptyDelete = this.platFormData.displayDeleteSuspicionColour === '#';

      },
      onInput() {
       //console.log(this.platFormData.scanRecogniseColour, this.platFormData.judgeRecogniseColour, this.platFormData.displayDeleteSuspicionColour)
        // this.isEmptyScan = this.platFormData.scanRecogniseColour === '#';
        // this.isEmptyJudge = this.platFormData.judgeRecogniseColour === '#';
        // this.isEmptyDelete = this.platFormData.displayDeleteSuspicionColour === '#';

      },
      getLocale() {
        return getLocale();
      },
      disableFromDeviceIdSelect(){

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
      disableSelect(){
        let context = document.getElementsByClassName("vs__selected");

        for(let i=0; i<context.length; i++){

          let span_text = context[i].textContent.trim();
          let btn = context[i].getElementsByTagName("button")[0];
          btn.removeAttribute("hidden");
          if(span_text===this.dataStorageOptions[0].label || span_text===this.selectedDeviceName){
            btn.setAttribute("hidden", true);
          }
        }
      },
      changeCursor(){
        document.getElementsByClassName("colorpicker-chrome").style.cursor = "pointer";
      },
      checkPermItem(value) {
        return checkPermissionItem(value);
      },
      hideModal(modal) {
        this.$refs[modal].hide();
      },
      onSearchButton() {
        this.$refs.vuetable.refresh();
      },
      onResetButton() {
        this.filter = {
          deviceName: '',
          status: null
        };
      },
      onRefreshItem(data, index) {
        this.$refs.vuetable.reload();
      },
      onAction(action, data) {

        switch (action) {
          case 'modify':
            this.pageStatus = 'modify';
            this.selectedDeviceId = data.deviceId;
            this.initializeSpanFormData(data);
            break;
          case 'show':
            this.pageStatus = 'show';
            this.selectedDeviceId = data.deviceId;
            this.initializeSpanFormData(data);
            break;
          case 'activate':
            this.selectedDeviceId = data.scanParamsId;
            this.$refs['modal-active'].show();
            //this.updateItemStatus('1000000701');
            break;
          case 'inactivate':
            this.selectedDeviceId = data.scanParamsId;
            this.$refs['modal-inactive'].show();
            break;
          case 'back':
            this.selectedDeviceId = 0;
            this.pageStatus = 'table';
            break;
          case 'restart':
            break;
          default:
        }
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
          temp.deviceNumber = temp.device ? temp.device.deviceSerial : '';
          temp.deviceName = temp.device ? temp.device.deviceName : '';
          temp.fieldDesignation = temp.device.field ? temp.device.field.fieldDesignation : '';
          temp.siteName = temp.device && temp.device.field ? temp.device.field.fieldDesignation : '';
          transformed.data.push(temp);
        }
        return transformed
      },
      tableHttpFetch(apiUrl, httpOptions) {
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.vuetableItems.perPage,
          sort: httpOptions.params.sort,
          filter: this.filter
        });
      },
      onTablePaginationData(paginationData) {
        this.$refs.pagination.setPaginationData(paginationData)
      },
      onTableChangePage(page) {
        this.$refs.vuetable.changePage(page)
      },
      initializeSpanFormData(result) {

        this.submitted = false;
        for (let key in this.scanForm) {
          if (Object.keys(result).includes(key)) {
            this.scanForm[key] = result[key];
          }
          // else if (key === 'status') {
          //   this.scanForm.status = result.device ? result.device.status : null;
          // }
          // if (key === 'fromDeviceId') {
          //   this.scanForm.fromDeviceId = result.fromParamsList.length > 0 ? result.fromParamsList[0].fromDeviceId : null;
          // }
        }
        this.scanForm.fromDeviceId = [];
        this.scanForm.fromDeviceId.push({
          value: result.deviceId,
          label: result.device.deviceName
        });
        this.selectedDeviceName = result.device.deviceName;
        if (result !== null) {
          result.fromParamsList.forEach(item => {
            if(item.device.deviceId!==result.deviceId) {
              this.scanForm.fromDeviceId.push({
                value: item.device.deviceId,
                label: item.device.deviceName

              })
            }
          })
        }

      },
      //update status
      updateItemStatus(statusValue) {
        if (this.selectedDeviceId === 0)
          return false;
        getApiManager()
          .post(`${apiBaseUrl}/system-setting/scan-param/update`, {
            scanParamsId: this.selectedDeviceId,
            status: statusValue
          })
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`device-management.device-list.status-updated-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                this.scanForm.status = statusValue;
                  this.$refs.vuetable.refresh();
                break;

            }
          })
          .catch((error) => {
          });
        this.$refs['modal-inactive'].hide();
        this.$refs['modal-active'].hide();
      },
      //save scanform
      onSaveScanFormData() {
        this.$v.scanForm.$touch();
        if (this.$v.scanForm.$invalid) {
          if(this.$v.scanForm.airCaliWarnTime.$invalid){
            if(this.scanForm.airCaliWarnTime === '') {
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.air-cali-time`), {
                duration: 3000,
                permanent: false
              });
              return;
            }
            else {
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.air-cali-time-valid`), {
                duration: 3000,
                permanent: false
              });
              return;
            }
          }
          if (this.$v.scanForm.standByTime.$invalid) {
            if(this.scanForm.standByTime==='') {
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.standby-time`), {
                duration: 3000,
                permanent: false
              });
              return;
            }
            else {
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.standby-time-valid`), {
                duration: 3000,
                permanent: false
              });
              return;
            }
          }
          if (this.$v.scanForm.deviceStorageAlarm.$invalid) {
            if(this.scanForm.deviceStorageAlarm==='') {
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.device-storage-alarm`), {
                duration: 3000,
                permanent: false
              });
              return;
            }
            else {
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.device-storage-alarm-valid`), {
                duration: 3000,
                permanent: false
              });
              return;
            }
          }
          // if (this.$v.scanForm.deviceStorageAlarmPercent.$invalid) {
          //   if(this.scanForm.deviceStorageAlarmPercent==='') {
          //     this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.device-storage-alarm-percent`), {
          //       duration: 3000,
          //       permanent: false
          //     });
          //     return;
          //   }
          //   else {
          //     this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.device-storage-alarm-percent-valid`), {
          //       duration: 3000,
          //       permanent: false
          //     });
          //     return;
          //   }
          // }
          return;
        }
        if (this.scanForm.fromDeviceId.length === 0) {
          this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.required-from-device-list`), {
            duration: 3000,
            permanent: false
          });
          return false;
        }
        this.scanForm.fromDeviceIdList = [];
        this.scanForm.fromDeviceId.forEach(item => {
          if(item.label!==this.selectedDeviceName) {
            this.scanForm.fromDeviceIdList.push(item.value);
          }
        });
        this.isLoading = true;
        getApiManager().post(`${apiBaseUrl}/system-setting/scan-param/modify`, this.scanForm).then((response) => {
          let message = response.data.message;
          let result = response.data.data;
          switch (message) {
            case responseMessages['ok']:
              this.$notify('success', this.$t('permission-management.permission-control.success'), this.$t(`system-setting.parameter-setting.saved-successful`), {
                duration: 3000,
                permanent: false
              });
              this.pageStatus = 'table';
              this.$refs.vuetable.reload();
              break;
          }
          this.isLoading = false;
        });
        this.$v.scanForm.$reset();
      },
      getScanParamsData() {
        getApiManagerError().post(`${apiBaseUrl}/system-setting/scan-param/get-all`).then((response) => {
          let message = response.data.message;
          let result = response.data.data;
          switch (message) {
            case responseMessages['ok']:
              this.scanParams = result;
          }
        });
      },
      getPlatFormData() {
        getApiManagerError().post(`${apiBaseUrl}/system-setting/platform-check/get`).then((response) => {
          let message = response.data.message;
          let result = response.data.data;
          switch (message) {
            case responseMessages['ok']:
              if (result.length > 0) {
                result = result[0];
                for (let key in this.platFormData) {
                  if (Object.keys(result).includes(key)) {
                    this.platFormData[key] = result[key];
                  }
                }
                this.platFormData.historyDataStorageSelect = [];
                this.platFormData.historyDataExportSelect = [];
                if (result.historyDataStorageList!==null && result.historyDataStorageList.length > 0) {
                  result.historyDataStorageList.forEach(item => {
                    this.platFormData.historyDataStorageSelect.push(findDicTextData(this.dataStorageOptions, item, false))
                  });
                }else {
                  this.platFormData.historyDataStorageSelect.push(this.dataStorageOptions[0]);
                }
                if (result.historyDataExportList!==null && result.historyDataExportList.length > 0) {
                  result.historyDataExportList.forEach(item => {
                    this.platFormData.historyDataExportSelect.push(findDicTextData(this.dataStorageOptions, item, false))
                  });
                }else {
                  this.platFormData.historyDataExportSelect.push(this.dataStorageOptions[0]);
                }
              }

          }
        });
      },
      getPlatFormOtherData() {
        getApiManagerError().post(`${apiBaseUrl}/system-setting/platform-other/get`).then((response) => {
          let message = response.data.message;
          let result = response.data.data;
          switch (message) {
            case responseMessages['ok']:
              if (result.length > 0) {
                result = result[0];
                for (let key in this.platFormOtherData) {
                  if (Object.keys(result).includes(key)) {
                    this.platFormOtherData[key] = result[key];
                  }

                }
              }
          }
        });
      },
      savePlatFormData() {
        //save platform main data
        if (this.tabIndex === 0) {
          this.$v.platFormData.$touch();
          console.log(this.platFormData.scanRecogniseColour, this.platFormData.judgeRecogniseColour, this.platFormData.displayDeleteSuspicionColour);
          if (this.$v.platFormData.$invalid) {
            if(this.$v.platFormData.scanRecogniseColour.$invalid){
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.atr-suspect-box-color-format`), {
                  duration: 3000,
                  permanent: false
                });
                return;
            }
            if (this.$v.platFormData.scanOverTime.$invalid) {
              if(this.platFormData.scanOverTime==='') {
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.work-timeout-reminder-input`), {
                  duration: 3000,
                  permanent: false
                });
                return;
              }
              else {
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.work-timeout-reminder-valid`), {
                  duration: 3000,
                  permanent: false
                });
                return;
              }
            }
            if (this.$v.platFormData.judgeAssignTime.$invalid) {
              if(this.platFormData.judgeAssignTime==='') {
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.dispatch-timeout-input`), {
                  duration: 3000,
                  permanent: false
                });
                return;
              }
              else {
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.dispatch-timeout-valid`), {
                  duration: 3000,
                  permanent: false
                });
                return;
              }
            }
            if (this.$v.platFormData.judgeProcessingTime.$invalid) {
              if(this.platFormData.judgeProcessingTime==='') {
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.processing-timeout-period-input`), {
                  duration: 3000,
                  permanent: false
                });
                return;
              }
              else {
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.processing-timeout-period-valid`), {
                  duration: 3000,
                  permanent: false
                });
                return;
              }
            }
            if (this.$v.platFormData.judgeScanOvertime.$invalid) {
              if(this.platFormData.judgeScanOvertime==='') {
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.judge-timeout-reminder-input`), {
                  duration: 3000,
                  permanent: false
                });
                return;
              }
              else {
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.judge-timeout-reminder-valid`), {
                  duration: 3000,
                  permanent: false
                });
                return;
              }
            }
            if (this.$v.platFormData.handOverTime.$invalid) {
              if(this.platFormData.handOverTime==='') {
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.hand-timeout-reminder-input`), {
                  duration: 3000,
                  permanent: false
                });
                return;
              }
              else {
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.hand-timeout-reminder-valid`), {
                  duration: 3000,
                  permanent: false
                });
                return;
              }
            }
            if (this.$v.platFormData.judgeRecogniseColour.$invalid) {
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.judgement-frame-color-format`), {
                duration: 3000,
                permanent: false
              });
              return;
            }
            if (this.$v.platFormData.displayDeleteSuspicionColour.$invalid) {
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.deleted-suspected-box-color-input`), {
                duration: 3000,
                permanent: false
              });
              return;
            }
            if (this.platFormData.historyDataStorageSelect.length === 0) {
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.required-history-data-storage`), {
                duration: 3000,
                permanent: false
              });
              return;
            }
            if (this.platFormData.historyDataExportSelect.length === 0) {
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.required-history-data-export`), {
                duration: 3000,
                permanent: false
              });
              return;
            }
            return;
          }

          //c
          this.platFormData.historyDataStorageList = [];
          this.platFormData.historyDataExportList = [];
          this.platFormData.historyDataStorageSelect.forEach(item => {
            this.platFormData.historyDataStorageList.push(item.value);
          });
          this.platFormData.historyDataExportSelect.forEach(item => {
            this.platFormData.historyDataExportList.push(item.value);
          });

          getApiManager().post(`${apiBaseUrl}/system-setting/platform-check/modify`, this.platFormData
          ).then((response) => {
            //getApiManagerError().post(`${apiParamUrl}`, this.platFormData);
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']:
                this.$notify('success', this.$t('permission-management.permission-control.success'), this.$t(`system-setting.setting-updated-successful`), {
                  duration: 3000,
                  permanent: false
                });
                break;
            }
          });
        }
        else { //save platform other data
          this.submitted = true;
          this.$v.platFormOtherData.$touch();

          if(parseInt(this.platFormOtherData.deviceTrafficHigh) <= parseInt(this.platFormOtherData.deviceTrafficMiddle)){
            this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.security-instrument-flow-high-middle`), {
              duration: 3000,
              permanent: false
            });
            return;
          }
          if (this.$v.platFormOtherData.$invalid) {
            if (this.$v.platFormOtherData.initialPassword.$invalid) {
              if(this.platFormOtherData.initialPassword==='') {
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.please-enter-password`), {
                  duration: 3000,
                  permanent: false
                });
                return;
              }
              else if(this.platFormOtherData.initialPassword.length <6 || this.platFormOtherData.initialPassword.length>20) {
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`password-reset.password-length`), {
                  duration: 3000,
                  permanent: false
                });
                return;
              }
              else {
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.password-valid`), {
                  duration: 3000,
                  permanent: false
                });
                return;
              }
            }
            if (this.$v.platFormOtherData.loginNumber.$invalid) {
              if(this.platFormOtherData.loginNumber==='') {
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.login-fail-count-input`), {
                  duration: 3000,
                  permanent: false
                });
                return;
              }
              else {
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.login-fail-count-valid`), {
                  duration: 3000,
                  permanent: false
                });
                return;
              }
            }
            if (this.$v.platFormOtherData.operatingTimeLimit.$invalid) {
              if(this.platFormOtherData.operatingTimeLimit==='') {
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.operating-time-limit-input`), {
                  duration: 3000,
                  permanent: false
                });
                return;
              }
              else {
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.operating-time-limit-valid`), {
                  duration: 3000,
                  permanent: false
                });
                return;
              }
            }
            if (this.$v.platFormOtherData.logMaxNumber.$invalid) {
              if(this.platFormOtherData.logMaxNumber==='') {
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.dispatch-timeout-input`), {
                  duration: 3000,
                  permanent: false
                });
                return;
              }
              else {
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.dispatch-timeout-valid`), {
                  duration: 3000,
                  permanent: false
                });
                return;
              }
            }
            if (this.$v.platFormOtherData.historyDataCycle.$invalid) {
              if(this.platFormOtherData.historyDataCycle==='') {
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.history-save-period-input`), {
                  duration: 3000,
                  permanent: false
                });
                return;
              }
              else {
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.history-save-period-valid`), {
                  duration: 3000,
                  permanent: false
                });
                return;
              }
            }
            if (this.$v.platFormOtherData.storageAlarm.$invalid) {
              if(this.platFormOtherData.storageAlarm==='') {
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.storage-warning-size-input`), {
                  duration: 3000,
                  permanent: false
                });
                return;
              }
              else {
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.storage-warning-size-valid`), {
                  duration: 3000,
                  permanent: false
                });
                return;
              }
            }
            if (this.$v.platFormOtherData.storageDetectionCycle.$invalid) {
              if(this.platFormOtherData.storageDetectionCycle==='') {
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.storage-check-period-input`), {
                  duration: 3000,
                  permanent: false
                });
                return;
              }
              else {
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.storage-check-period-valid`), {
                  duration: 3000,
                  permanent: false
                });
                return;
              }
            }

            if (this.$v.platFormOtherData.deviceTrafficHigh.$invalid) {
              if(this.platFormOtherData.deviceTrafficHigh==='') {
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.security-instrument-flow-high-input`), {
                  duration: 3000,
                  permanent: false
                });
                return;
              }
              else {
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.security-instrument-flow-high-valid`), {
                  duration: 3000,
                  permanent: false
                });
                return;
              }
            }
            if (this.$v.platFormOtherData.deviceTrafficMiddle.$invalid) {
              if(this.platFormOtherData.deviceTrafficMiddle==='') {
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.security-instrument-flow-middle-input`), {
                  duration: 3000,
                  permanent: false
                });
                return;
              }
              else {
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`system-setting.parameter-setting.security-instrument-flow-middle-valid`), {
                  duration: 3000,
                  permanent: false
                });
                return;
              }
            }
            return;
          }
          getApiManager().post(`${apiBaseUrl}/system-setting/platform-other/modify`, this.platFormOtherData
          ).then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']:
                this.$notify('success', this.$t('permission-management.permission-control.success'), this.$t(`system-setting.setting-updated-successful`), {
                  duration: 3000,
                  permanent: false
                });
                break;
            }
          });
        }
      },
    }
  }
</script>

