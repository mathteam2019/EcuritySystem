<template>
  <div class="system-setting">
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb/>
        </b-colxx>
      </b-row>
    </div>

    <b-tabs nav-class="ml-2" :no-fade="true">
      <b-tab :title="$t('system-setting.parameter-setting.platform-parameter')">

        <div class="section pt-0 mx-3">
          <b-tabs class="sub-tabs" v-model="tabIndex" card>
            <b-tab :title="$t('menu.personal-inspection')">
              <div>
                <b-row>
                  <b-col cols="1">
                    <label class="font-weight-bold">{{$t('system-setting.parameter-setting.scan')}}</label>
                  </b-col>
                  <b-col cols="2">
                    <b-form-group :label="$t('system-setting.parameter-setting.atr-suspect-box-color')">
                      <colorpicker :color="platFormData.scanRecogniseColour"
                                   v-model="platFormData.scanRecogniseColour"/>
                    </b-form-group>
                  </b-col>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.work-timeout-reminder')">
                      <b-form-input v-model="platFormData.scanOverTime"></b-form-input>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col cols="1">
                    <label class="font-weight-bold">{{$t('system-setting.parameter-setting.judgement')}}</label>
                  </b-col>
                  <b-col cols="2">
                    <b-form-group :label="$t('system-setting.parameter-setting.dispatch-timeout')">
                      <b-form-input v-model="platFormData.judgeAssignTime"></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.processing-timeout-period')">
                      <b-form-input v-model="platFormData.judgeProcessingTime"></b-form-input>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.work-timeout-reminder')">
                      <b-form-input v-model="platFormData.judgeScanOvertime"></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.judgement-frame-color')">
                      <colorpicker :color="platFormData.judgeRecogniseColour"
                                   v-model="platFormData.judgeRecogniseColour"/>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col cols="1">
                    <label class="font-weight-bold">{{$t('system-setting.parameter-setting.history-task')}}</label>
                  </b-col>
                  <b-col cols="2">
                    <b-form-group :label="$t('system-setting.parameter-setting.data-storage')">
                      <b-form-select v-model="platFormData.historyDataStorage" :options="dataStorageOptions"
                                     plain></b-form-select>
                    </b-form-group>
                  </b-col>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.data-output')">
                      <b-form-select v-model="platFormData.historyDataExport" :options="dataStorageOptions"
                                     plain></b-form-select>
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
                      <b-form-input v-model="platFormData.displayDeleteSuspicion"></b-form-input>
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
                      <colorpicker :color="platFormData.displayDeleteSuspicionColour"
                                   v-model="platFormData.displayDeleteSuspicionColour"/>
                    </b-form-group>
                  </b-col>
                </b-row>
              </div>
            </b-tab>
            <b-tab :title="$t('permission-management.other')">
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
                      <b-form-input type="password" v-model="platFormOtherData.initialPassword"></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.login-fail-count')">
                      <!--<label>{{platFormOtherData.loginNumber}}</label>-->
                      <b-form-input v-model="platFormOtherData.loginNumber"></b-form-input>
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
                      <b-form-input v-model="platFormOtherData.logMaxNumber"></b-form-input>
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
                                     plain></b-form-select>
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
                      <b-form-input v-model="platFormOtherData.storageDetectionCycle"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.storage-warning-size')">
                      <b-form-input v-model="platFormOtherData.storageAlarm"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.history-save-period')">
                      <b-form-input v-model="platFormOtherData.historyDataCycle"></b-form-input>
                    </b-form-group>
                  </b-col>
                </b-row>

              </div>
            </b-tab>
          </b-tabs>
        </div>

        <div class="text-right mr-3 mt-3">
          <b-button size="sm" variant="info default" class="mr-3" @click="savePlatFormData()">
            <i class="icofont-save"></i>
            {{$t('permission-management.permission-control.save')}}
          </b-button>
          <b-button size="sm" variant="info default">
            <i class="icofont-long-arrow-left"></i>
            {{$t('permission-management.return')}}
          </b-button>
        </div>

      </b-tab>

      <b-tab :title="$t('system-setting.parameter-setting.security-instrument')">
        <b-row v-if="pageStatus === 'table'" class="h-100 ">
          <b-col cols="12 d-flex flex-column">
            <b-row class="pt-2">
              <b-col cols="6">
                <b-row>

                  <b-col>
                    <b-form-group :label="$t('log-management.device-log.device')">
                      <b-form-input v-model="filter.deviceName"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('permission-management.active')">
                      <b-form-select v-model="filter.status" :options="stateOptions" plain/>
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
                      <span class="cursor-p text-primary" @click="onAction('modify', props.rowData)">{{ props.rowData.deviceNumber }}</span>
                    </template>
                    <template slot="operating" slot-scope="props">
                      <div>

                        <b-button
                          size="sm"
                          variant="info default btn-square"
                          @click="onAction('modify', props.rowData)">
                          <i class="icofont-edit"></i>
                        </b-button>

                        <b-button
                          size="sm"
                          variant="success default btn-square"
                          @click="onAction('restart', props.rowData)">
                          <i class="icofont-refresh"></i>
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
                  ></vuetable-pagination-bootstrap>
                </div>
              </b-col>
            </b-row>
          </b-col>
        </b-row>
        <b-row v-if="pageStatus !== 'table'" class="h-100 form-section">
          <b-col cols="10">
            <b-row class="mb-2">
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('system-setting.parameter-setting.cover-correction-time')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input v-model="scanForm.airCaliWarnTime"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('system-setting.standby-time')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input v-model="scanForm.standByTime"></b-form-input>
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
                  <b-form-select v-model="scanForm.chestBlurring" :options="bitOptions" plain/>
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
                <b-form-group>
                  <template slot="label">
                    {{$t('system-setting.parameter-setting.suitable-for')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-select v-model="scanForm.fromDeviceId" :options="deviceSelectOptions" plain/>
                </b-form-group>
              </b-col>
            </b-row>

          </b-col>
          <b-col cols="12" class="d-flex justify-content-end align-self-end">
            <div>
              <b-button v-if="scanForm.status === 'inactive'" @click="onAction('activate')" variant="success default" size="sm"><i
                class="icofont-check-circled"></i> {{
                $t('permission-management.action-make-active') }}
              </b-button>
              <b-button v-if="scanForm.status === 'active'" @click="onAction('inactivate')" variant="warning default" size="sm"><i
                class="icofont-ban"></i> {{
                $t('permission-management.action-make-inactive') }}
              </b-button>
              <b-button v-if="scanForm.status === 'inactive'" @click="onSaveScanFormData()" variant="success default" size="sm"><i class="icofont-save"></i>
                {{ $t('permission-management.save-button') }}
              </b-button>
              <b-button @click="onAction('back')" variant="info default" size="sm"><i
                class="icofont-long-arrow-left"></i> {{
                $t('permission-management.return') }}
              </b-button>
            </div>
          </b-col>
          <div class="position-absolute" style="left: 8%;bottom: 3%">
            <img v-if="scanForm.status === 'inactive'" src="../../../assets/img/no_active_stamp.png">
            <img v-else-if="scanForm.status === 'active'" src="../../../assets/img/active_stamp.png">
          </div>

        </b-row>
      </b-tab>
    </b-tabs>
    <b-modal centered id="modal-inactive" ref="modal-inactive" :title="$t('system-setting.prompt')">
      {{$t('device-management.device-list.make-inactive-prompt')}}
      <template slot="modal-footer">
        <b-button variant="primary" @click="updateItemStatus('inactive')" class="mr-1">
          {{$t('system-setting.ok')}}
        </b-button>
        <b-button variant="danger" @click="hideModal('modal-inactive')">{{$t('system-setting.cancel')}}
        </b-button>
      </template>
    </b-modal>
  </div>
</template>

<style lang="scss">
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

</style>

<script>
  import Vuetable from '../../../components/Vuetable2/Vuetable'
  import VuetablePagination from 'vuetable-2/src/components/VuetablePagination'
  import VuetablePaginationBootstrap from '../../../components/Common/VuetablePaginationBootstrap'
  import {getApiManager, getDateTimeWithFormat} from '../../../api';
  import {responseMessages} from '../../../constants/response-messages';
  import {apiBaseUrl} from "../../../constants/config";
  import ColorPicker from '../../../components/ColorPicker/VueColorPicker'

  export default {
    components: {
      'vuetable': Vuetable,
      'vuetable-pagination': VuetablePagination,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
      'colorpicker': ColorPicker
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
            text: scan.device ? scan.device.deviceName : 'dev-' + scan.deviceId
          });
        });
        this.deviceSelectOptions = selectOptions;
      },
      'scanForm.fromDeviceId': function (newVal, oldVal) {
        //when initialize data, need to skip
        if (oldVal !== null)
          this.getScanParamsDetail(newVal);
      }
    },
    data() {
      return {
        tabIndex: 0,
        pageStatus: 'table',
        filter: {
          deviceName: '',
          status: null
        },
        stateOptions: [
          {value: null, text: this.$t('permission-management.all')},
          {value: 'active', text: this.$t('permission-management.active')},
          {value: 'inactive', text: this.$t('permission-management.inactive')}
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
              name: 'scanParamsId',
              title: this.$t('permission-management.th-no'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:deviceNumber',
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
              name: 'configValue',
              title: this.$t('system-setting.parameter-setting.configuration'),
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
          {value: 'yes', text: this.$t('system-setting.parameter-setting.yes')},
          {value: 'no', text: this.$t('system-setting.parameter-setting.no')},
        ],
        bitOptions: [
          {value: 1, text: 1},
          {value: 0, text: 0},
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
          historyDataStorage: null,
          historyDataExport: null,
          displayDeleteSuspicion: null,
          displayDeleteSuspicionColour: null,
        },
        platFormOtherData: {
          id: 0,
          initialPassword: null,
          loginNumber: null,
          logMaxNumber: null,
          deviceTrafficSettings: null,
          storageDetectionCycle: null,
          storageAlarm: null,
          historyDataCycle: null,
        },
        dataStorageOptions: [
          {value: 'business', text: this.$t('system-setting.storage-business')},
          {value: 'cartoon', text: this.$t('system-setting.storage-cartoon')},
          {value: 'conversion', text: this.$t('system-setting.storage-conversion')},
          {value: 'original', text: this.$t('system-setting.storage-original')},
        ],
        levelOptions: [
          {value: 'low', text: this.$t('system-setting.level-low')},
          {value: 'middle', text: this.$t('system-setting.level-middle')},
          {value: 'high', text: this.$t('system-setting.level-high')},
        ],
        scanForm: {
          scanParamsId: 0,
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
          fromDeviceId: null,
          status: null,
        },
      }
    },
    methods: {
      hideModal(modal) {
        this.$refs[modal].hide();
      },
      onAction(action, data) {
        switch (action) {
          case 'modify':
            this.pageStatus = 'modify';
            this.selectedDeviceId = data.deviceId;
            this.initializeSpanFormData(data);
            break;
          case 'activate':
            this.updateItemStatus('active');
            break;
          case 'inactivate':
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
          /* temp.siteName = temp.device ? temp.device.field.fieldDesignation : '';*/
           temp.configValue = temp.fromParamsList.length > 0 ? temp.fromParamsList[0].device.deviceName : "";
          transformed.data.push(temp);
        }
        return transformed
      },
      tableHttpFetch(apiUrl, httpOptions) {
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.vuetableItems.perPage,
          filter: this.filter
        });
      },
      onTablePaginationData(paginationData) {
        this.$refs.pagination.setPaginationData(paginationData)
      },
      onTableChangePage(page) {
        this.$refs.vuetable.changePage(page)
      },
      getScanParamsDetail(deviceId) {
        for (let item of this.scanParams) {
          if (item.deviceId === deviceId) {
            this.initializeSpanFormData(item);
            break;
          }
        }

      },
      initializeSpanFormData(result) {
        for (let key in this.scanForm) {
          if (Object.keys(result).includes(key)) {
            this.scanForm[key] = result[key];
          } else if(key === 'status'){
            this.scanForm.status = result.device?result.device.status:null;
          }
          else if (key === 'fromDeviceId') {
            this.scanForm.fromDeviceId = result.fromParamsList.length > 0 ? result.fromParamsList[0].fromDeviceId : null;
          }
        }
      },
      //update status
      updateItemStatus(statusValue) {
        if (this.selectedDeviceId === 0)
          return false;
        getApiManager()
          .post(`${apiBaseUrl}/device-management/device-table/device/update-status`, {
            deviceId: this.selectedDeviceId,
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
                if (this.pageStatus === 'table')
                  this.$refs.vuetable.refresh();
                break;

            }
          })
          .catch((error) => {
          });
        this.$refs['modal-inactive'].hide();
      },
      //save scanform
      onSaveScanFormData(){
        this.scanForm.deviceId = this.selectedDeviceId;
        getApiManager().post(`${apiBaseUrl}/system-setting/scan-param/modify`,this.scanForm).then((response) => {
          let message = response.data.message;
          let result = response.data.data;
          switch (message) {
            case responseMessages['ok']:
              this.$notify('success', this.$t('permission-management.permission-control.success'), this.$t(`system-setting.parameter-setting.saved-successful`), {
                duration: 3000,
                permanent: false
              });
              this.pageStatus = 'table';
              break;
          }
        });
      },
      getScanParamsData() {
        getApiManager().post(`${apiBaseUrl}/system-setting/scan-param/get-all`).then((response) => {
          let message = response.data.message;
          let result = response.data.data;
          switch (message) {
            case responseMessages['ok']:
              this.scanParams = result;
          }
        });
      },
      getPlatFormData() {
        getApiManager().post(`${apiBaseUrl}/system-setting/platform-check/get`).then((response) => {
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
              }

          }
        });
      },
      getPlatFormOtherData() {
        getApiManager().post(`${apiBaseUrl}/system-setting/platform-other/get`).then((response) => {
          let message = response.data.message;
          let result = response.data.data;
          switch (message) {
            case responseMessages['ok']:
              if (result.length > 0) {
                result = result[0];
                for (let key in this.platFormOtherData) {
                  if (Object.keys(result).includes(key))
                    this.platFormOtherData[key] = result[key];
                }
              }
          }
        });
      },
      savePlatFormData() {
        //save platform main data
        if (this.tabIndex === 0) {
          getApiManager().post(`${apiBaseUrl}/system-setting/platform-check/modify`, this.platFormData
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
        else { //save platform other data
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

