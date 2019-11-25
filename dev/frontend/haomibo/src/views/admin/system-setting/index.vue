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
          <b-tabs class="sub-tabs" card>
            <b-tab :title="$t('menu.personal-inspection')">
              <div>
                <b-row>
                  <b-col cols="1">
                    <label class="font-weight-bold">{{$t('system-setting.parameter-setting.scan')}}</label>
                  </b-col>
                  <b-col cols="2">
                    <b-form-group :label="$t('system-setting.parameter-setting.atr-suspect-box-color')">
                      <b-form-input></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.work-timeout-reminder')">
                      <b-form-input></b-form-input>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col cols="1">
                    <label class="font-weight-bold">{{$t('system-setting.parameter-setting.judgement')}}</label>
                  </b-col>
                  <b-col cols="2">
                    <b-form-group :label="$t('system-setting.parameter-setting.dispatch-timeout')">
                      <b-form-input></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.processing-timeout-period')">
                      <b-form-input></b-form-input>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.work-timeout-reminder')">
                      <b-form-input></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.judgement-frame-color')">
                      <b-form-input></b-form-input>
                    </b-form-group>
                  </b-col>
                </b-row>

                <b-row>
                  <b-col cols="1">
                    <label class="font-weight-bold">{{$t('system-setting.parameter-setting.history-task')}}</label>
                  </b-col>
                  <b-col cols="2">
                    <b-form-group :label="$t('system-setting.parameter-setting.data-storage')">
                      <b-form-select plain></b-form-select>
                    </b-form-group>
                  </b-col>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.data-output')">
                      <b-form-select plain></b-form-select>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.seized-item-classification')">
                      <b-form-input></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.seized-items')">
                      <b-form-input></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.check-item-level')">
                      <b-form-input></b-form-input>
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
                      <b-form-input></b-form-input>
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
                      <b-form-input></b-form-input>
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
                      <b-form-input type="password"></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.login-fail-count')">
                      <label>7</label>
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
                      <b-form-input></b-form-input>
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
                      <b-form-select plain></b-form-select>
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
                      <b-form-input></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.storage-warning-size')">
                      <b-form-input></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.history-save-period')">
                      <b-form-input></b-form-input>
                    </b-form-group>
                  </b-col>
                </b-row>

              </div>
            </b-tab>
          </b-tabs>
        </div>

        <div class="text-right mr-3 mt-3">
          <b-button size="sm" variant="info default" class="mr-3">
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
                    <b-form-group :label="$t('system-setting.site')">
                      <b-form-select v-model="filter.siteName" :options="[]" plain/>
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
                    :api-mode="false"
                    :fields="vuetableItems.fields"
                    :per-page="vuetableItems.perPage"
                    :data="tempData"
                    data-path="data"
                    pagination-path="pagination"
                    :data-total="tempData.data.length"
                    track-by="deviceId"
                    class="table-striped"
                    @vuetable:pagination-data="onTablePaginationData"
                  >
                    <template slot="deviceNumber" slot-scope="props">
                      <span class="cursor-p text-primary" @click="onAction('show', props.rowData, props.rowIndex)">{{ props.rowData.deviceNumber }}</span>
                    </template>
                    <template slot="operating" slot-scope="props">
                      <div>

                        <b-button
                          size="sm"
                          variant="info default btn-square"
                          @click="onAction('modify', props.rowData, props.rowIndex)">
                          <i class="icofont-edit"></i>
                        </b-button>

                        <b-button
                          size="sm"
                          variant="success default btn-square"
                          @click="onAction('restart', props.rowData, props.rowIndex)">
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
                  <b-form-select :options="[]" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('system-setting.standby-time')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-select :options="[]" plain/>
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
                  <b-form-select :options="yesNoOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('system-setting.parameter-setting.pass-prompt')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-select :options="yesNoOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('system-setting.parameter-setting.pos-error-prompt')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-select :options="yesNoOptions" plain/>
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
                  <b-form-select :options="yesNoOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('system-setting.parameter-setting.while-scan')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-select :options="yesNoOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('system-setting.parameter-setting.complete-scan-prompt')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-select :options="yesNoOptions" plain/>
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
                  <b-form-select :options="yesNoOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('system-setting.parameter-setting.auxiliary-recognition-level')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-select :options="[]" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('system-setting.parameter-setting.save-history-image')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-select :options="yesNoOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('system-setting.parameter-setting.save-only-suspected-image')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-select :options="yesNoOptions" plain/>
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
                  <b-form-select :options="yesNoOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('system-setting.chest-blurring')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-select :options="[]" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('system-setting.hip-blurring')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-select :options="yesNoOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">
                    {{$t('system-setting.groin-blurring')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-select :options="yesNoOptions" plain/>
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
                  <b-form-select :options="[]" plain/>
                </b-form-group>
              </b-col>
            </b-row>

          </b-col>
          <b-col cols="12" class="d-flex justify-content-end align-self-end">
            <div>
              <b-button @click="onDetailAction('deactivate')" variant="warning default" size="sm"><i
                class="icofont-ban"></i> {{
                $t('permission-management.action-make-inactive') }}
              </b-button>
              <b-button @click="onDetailAction('back')" variant="info default" size="sm"><i
                class="icofont-long-arrow-left"></i> {{
                $t('permission-management.return') }}
              </b-button>
            </div>
          </b-col>
          <div class="position-absolute" style="left: 8%;bottom: 8%">
            <img src="../../../assets/img/no_active_stamp.png">
          </div>

        </b-row>
      </b-tab>
    </b-tabs>
  </div>
</template>

<style lang="scss">
  .vc-chrome {
    position: absolute;
    top: 35px;
    right: 0;
    z-index: 9;
  }

  .current-color {
    display: inline-block;
    width: 16px;
    height: 16px;
    background-color: #000;
    cursor: pointer;
  }

</style>

<script>
  import InputTag from '../../../components/Form/InputTag';
  import vSelect from 'vue-select'
  import Vuetable from '../../../components/Vuetable2/Vuetable'
  import VuetablePagination from 'vuetable-2/src/components/VuetablePagination'
  import VuetablePaginationBootstrap from '../../../components/Common/VuetablePaginationBootstrap'
  import Switches from 'vue-switches'
  import 'vue-select/dist/vue-select.css'

  export default {
    components: {
      'input-tag': InputTag,
      'v-select': vSelect,
      'vuetable': Vuetable,
      'vuetable-pagination': VuetablePagination,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
      'switches': Switches,
    },
    data() {
      return {
        pageStatus: 'table',
        filter: {
          deviceName: '',
          siteName: ''
        },
        tempData: {
          pagination: {
            "total": 4,
            "per_page": 5,
            "current_page": 1,
            "last_page": 1,
            "from": 1,
            "to": 4,
          },
          data: [
            {
              deviceId: 1,
              deviceNumber: '0001'
            },
            {
              deviceId: 2,
              deviceNumber: '0002'
            },
            {
              deviceId: 3,
              deviceNumber: '0003'
            },
            {
              deviceId: 4,
              deviceNumber: '0004'
            },
          ]
        },
        vuetableItems: {
          perPage: 10,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'deviceId',
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
              name: 'config',
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
        yesNoOptions: [
          this.$t('system-setting.parameter-setting.yes'),
          this.$t('system-setting.parameter-setting.no')
        ],
        switchValue: false,
        formData: {
          standbyTime: 0,
          alarmSound: true,
          passSound: true,
          posErrorSound: true,
          startSound: false,
          scanSound: false,
          scanCompleteSound: false,
          autoRecognition: true,
          recognitionRate: 0,
          atrRectColor: {
            hex: '#FF0000',
          },
          recordDeleteRect: true,
          checkDiskPeriod: 60,
          warnDiskSpace: 15,
          historyImageDeletePeriod: 0,
          saveScanData: true,
          saveSuspectData: false,
          facialBlurring: true,
          chestBlurring: true,
          hipBlurring: true,
          groinBlurring: true,
        }
      }
    },
    methods: {
      onTablePaginationData(paginationData) {
        this.$refs.pagination.setPaginationData(paginationData)
      },
      onTableChangePage(page) {
        this.$refs.vuetable.changePage(page)
      },
      onAction(action, data, index) {
        switch (action) {
          case 'show':
            this.pageStatus = 'show';
            break;
          case 'modify':
            this.pageStatus = 'modify';
            break;
          case 'restart':

            break;
          default:
        }
      },

      onDetailAction(action) {
        switch (action) {
          case 'deactivate':

            break;
          case 'back':
            this.pageStatus = 'table';
            break;
          default:

        }
      },

      onHorizontalSubmit() {
        console.log('submit form');
      },
    }
  }
</script>

