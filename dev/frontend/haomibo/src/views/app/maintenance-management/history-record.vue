<style lang="scss">
  .inline-form-no-margin {
    .form-group {
      margin-bottom: 0;
    }
  }
  .inline-label-form-section {
    .w-130-px {
      width: 130px !important;
    }
    .w-200-px {
      width: 200px !important;
    }
    input[type=time] {
      padding-left: 30px;
      position: relative;
      &:after {
        font-family: 'IcoFont' !important;
        content: "\eedc";
        position: absolute;
        left: 5px;
        font-size: 19px;
        color: #9c9999;
      }
    }
    input[type=number]:not(.full-width) {
      width: 60px;
    }
    div.front-icon {
      position: relative;
      display: flex;
      input {
        padding-left: 30px;
      }
      i {
        position: absolute;
        left: 5px;
        line-height: 39px;
        font-size: 19px;
        color: #9c9999;
      }
    }
  }
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
</style>
<template>
  <div>
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb/>
        </b-colxx>
      </b-row>
    </div>
    <b-card class="main-without-tab">
      <div v-if="pageStatus==='list'" class="h-100 d-flex flex-column">
        <b-row>
          <b-col cols="8">
            <b-row>
              <b-col>
                <b-form-group :label="$t('maintenance-management.process-task.task')">
                  <b-form-input v-model="filterForm.task"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group :label="$t('maintenance-management.process-task.position')">
                  <b-form-select v-model="filterForm.position" :options="positionOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group :label="$t('maintenance-management.history-record.device-classification')">
                  <b-form-select v-model="filterForm.deviceClassification" :options="deviceClassificationOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group :label="$t('maintenance-management.history-record.device')">
                  <b-form-input v-model="filterForm.device"></b-form-input>
                </b-form-group>
              </b-col>
            </b-row>
          </b-col>
          <b-col cols="4" class="d-flex justify-content-end align-items-center">
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
            </div>
          </b-col>
        </b-row>

        <b-row class="flex-grow-1">
          <b-col cols="12">
            <div class="table-wrapper table-responsive">
              <vuetable
                ref="pendingListTable"
                :api-mode="false"
                :fields="pendingListTableItems.fields"
                :data-manager="pendingListTableDataManager"
                :per-page="pendingListTableItems.perPage"
                pagination-path="pagination"
                @vuetable:pagination-data="onBlackListTablePaginationData"
                class="table-striped"
              >
                <div slot="number" slot-scope="props">
                <span class="cursor-p text-primary"
                      @click="onAction('show')">{{ props.rowData.number}}</span>
                </div>
              </vuetable>
            </div>
            <div class="pagination-wrapper">
              <vuetable-pagination-bootstrap
                ref="pendingListTablePagination"
                :initial-per-page="pendingListTableItems.perPage"
                @vuetable-pagination:change-page="onBlackListTableChangePage"
              ></vuetable-pagination-bootstrap>
            </div>
          </b-col>
        </b-row>

      </div>
      <div v-if="pageStatus==='show'" class="h-100 d-flex flex-grow-1 flex-column">
        <b-tabs  class="sub-tabs" nav-class="separator-tabs ml-0" content-class="tab-content"
                 :no-fade="true">
          <b-tab :title="$t('device-management.device-table.device-info')">
            <b-row class="h-100 form-section">
              <b-col cols="8">
                <b-row>
                  <b-col>
                    <b-form-group>
                      <template slot="label">
                        {{$t('personal-inspection.task-number')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label>HR201909010001</label>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group>
                      <template slot="label">
                        {{$t('personal-inspection.on-site')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label>北京首都机场</label>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group>
                      <template slot="label">
                        {{$t('personal-inspection.security-instrument')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label>安检仪001</label>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col>
                    <b-form-group>
                      <template slot="label">
                        {{$t('personal-inspection.task-number')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label>HR201909010001</label>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group>
                      <template slot="label">
                        {{$t('personal-inspection.on-site')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label>北京首都机场</label>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group>
                      <template slot="label">
                        {{$t('personal-inspection.security-instrument')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label>安检仪001</label>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col>
                    <b-form-group>
                      <template slot="label">
                        {{$t('personal-inspection.task-number')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label>HR201909010001</label>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group>
                      <template slot="label">
                        {{$t('personal-inspection.on-site')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label>北京首都机场</label>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group>
                      <template slot="label">
                        {{$t('personal-inspection.security-instrument')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label>安检仪001</label>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col>
                    <b-form-group>
                      <template slot="label">
                        {{$t('personal-inspection.task-number')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label>HR201909010001</label>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group>
                      <template slot="label">
                        {{$t('personal-inspection.on-site')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label>北京首都机场</label>
                    </b-form-group>
                  </b-col>
                  <b-col>
                  </b-col>
                </b-row>
              </b-col>
              <b-col cols="4" class="d-flex flex-column align-items-center">
                <div class="img-wrapper">
                  <img src="../../../assets/img/man-in-device.png">
                </div>
              </b-col>

            </b-row>
          </b-tab>
          <b-tab :title="$t('device-management.device-table.archive-info')">
            <b-row class="h-100 form-section inline-form-no-margin">
              <b-col cols="8">
                <b-row>
                  <b-col cols="4">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('personal-inspection.task-number')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label class="input-label">HR201909010001</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('personal-inspection.task-number')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label class="input-label">HR201909010001</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('personal-inspection.task-number')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label class="input-label">HR201909010001</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('personal-inspection.task-number')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label class="input-label">HR201909010001</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('personal-inspection.task-number')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label class="input-label">HR201909010001</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('personal-inspection.task-number')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label class="input-label">HR201909010001</label>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row class="mb-3">
                  <b-col cols="12" class="d-flex align-items-center">
                    <label class="pr-2 m-0 "
                           style="color: #bdbaba">{{$t('device-management.device-list.device-information')}}</label>
                    <div class="flex-grow-1" style="height: 1px;background-color: #bdbaba"></div>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col cols="4">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('personal-inspection.task-number')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label class="input-label">HR201909010001</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('personal-inspection.task-number')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label class="input-label">HR201909010001</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('personal-inspection.task-number')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label class="input-label">HR201909010001</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('personal-inspection.task-number')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label class="input-label">HR201909010001</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('personal-inspection.task-number')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label class="input-label">HR201909010001</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('personal-inspection.task-number')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label class="input-label">HR201909010001</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('personal-inspection.task-number')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label class="input-label">HR201909010001</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('personal-inspection.task-number')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label class="input-label">HR201909010001</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('personal-inspection.task-number')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label class="input-label">HR201909010001</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('personal-inspection.task-number')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label class="input-label">HR201909010001</label>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row class="mb-3">
                  <b-col cols="12" class="d-flex align-items-center">
                    <label class="pr-2 m-0 "
                           style="color: #bdbaba">{{$t('device-management.device-list.device-information')}}</label>
                    <div class="flex-grow-1" style="height: 1px;background-color: #bdbaba"></div>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col cols="4">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('personal-inspection.task-number')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label class="input-label">HR201909010001</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('personal-inspection.task-number')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label class="input-label">HR201909010001</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('personal-inspection.task-number')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label class="input-label">HR201909010001</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('personal-inspection.task-number')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label class="input-label">HR201909010001</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('personal-inspection.task-number')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label class="input-label">HR201909010001</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('personal-inspection.task-number')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label class="input-label">HR201909010001</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('personal-inspection.task-number')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label class="input-label">HR201909010001</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('personal-inspection.task-number')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label class="input-label">HR201909010001</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('personal-inspection.task-number')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label class="input-label">HR201909010001</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('personal-inspection.task-number')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label class="input-label">HR201909010001</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('personal-inspection.task-number')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label class="input-label">HR201909010001</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('personal-inspection.task-number')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label class="input-label">HR201909010001</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('personal-inspection.task-number')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label class="input-label">HR201909010001</label>
                    </b-form-group>
                  </b-col>
                </b-row>
              </b-col>
              <b-col cols="4" class="d-flex flex-column align-items-center">
                <div class="img-wrapper">
                  <img src="../../../assets/img/man-in-device.png">
                </div>
              </b-col>

            </b-row>
          </b-tab>
        </b-tabs>
        <div class="d-flex align-items-end justify-content-end flex-grow-1 mr-3 mb-3">
          <div>
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
                filterForm: {
                    task: null,
                    position: null,
                    deviceClassification: null,
                    device: null
                },
                positionOptions: [
                    {value: null, text: this.$t('permission-management.all')},
                    {value: 'position-1', text: '清理'},
                    {value: 'position-2', text: '维修'}
                ],
                deviceClassificationOptions: [
                    {value: null, text: this.$t('permission-management.all')},
                    {value: 'classification-1', text: '分类1'},
                    {value: 'classification-2', text: '分类2'},
                    {value: 'classification-3', text: '分类3'},
                ],
                weekDayOptions: [
                    {value: null, text: this.$t('maintenance-management.all')},
                    {value: 1, text: this.$t('maintenance-management.week-1')},
                    {value: 2, text: this.$t('maintenance-management.week-2')},
                    {value: 3, text: this.$t('maintenance-management.week-3')},
                    {value: 4, text: this.$t('maintenance-management.week-4')},
                    {value: 5, text: this.$t('maintenance-management.week-5')},
                    {value: 6, text: this.$t('maintenance-management.week-6')},
                    {value: 7, text: this.$t('maintenance-management.week-7')},
                ],
                pendingListTableItems: {
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
                            title: this.$t('maintenance-management.history-record.no'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: '__slot:number',
                            sortField: 'number',
                            title: this.$t('maintenance-management.history-record.number'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'task',
                            sortField: 'task',
                            title: this.$t('maintenance-management.history-record.task'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'position',
                            sortField: 'position',
                            title: this.$t('maintenance-management.history-record.position'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'deviceClassification',
                            sortField: 'deviceClassification',
                            title: this.$t('maintenance-management.history-record.device-classification'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'device',
                            sortField: 'device',
                            title: this.$t('maintenance-management.history-record.device'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'plan',
                            sortField: 'plan',
                            title: this.$t('maintenance-management.history-record.plan'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'securityDepartment',
                            sortField: 'security-department',
                            title: this.$t('maintenance-management.history-record.security-department'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'securityGuard',
                            sortField: 'securityGuard',
                            title: this.$t('maintenance-management.history-record.security-user'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'estimatedStartTime',
                            sortField: 'estimatedStartTime',
                            title: this.$t('maintenance-management.history-record.estimated-start-time'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'realStartTime',
                            sortField: 'realStartTime',
                            title: this.$t('maintenance-management.history-record.real-start-time'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'completeTime',
                            sortField: 'completeTime',
                            title: this.$t('maintenance-management.history-record.complete-time'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        }
                    ]
                },
                tempData: [
                    {
                        "no": 1,
                        "number": "C201909200001",
                    },
                    {
                        "no": 2,
                        "number": "C201909200002",
                    },
                    {
                        "no": 3,
                        "number": "C201909200003",
                    },
                    {
                        "no": 4,
                        "number": "C201909200004",
                    },
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
                    case 'show':
                        this.pageStatus = 'show';
                        break;
                    case 'show-list':
                        this.pageStatus = 'list';
                        break;
                }
            },
            pendingListTableDataManager(sortOrder, pagination) {
                let local = this.tempData;

                // sortOrder can be empty, so we have to check for that as well
                if (sortOrder.length > 0) {
                    local = _.orderBy(
                        local,
                        sortOrder[0].sortField,
                        sortOrder[0].direction
                    );
                }

                pagination = this.$refs.pendingListTable.makePagination(
                    local.length,
                    this.pendingListTableItems.perPage
                );

                let from = pagination.from - 1;
                let to = from + this.pendingListTableItems.perPage;
                return {
                    pagination: pagination,
                    data: _.slice(local, from, to)
                };
            },
            onBlackListTablePaginationData(paginationData) {
                this.$refs.pendingListTablePagination.setPaginationData(paginationData);
            },
            onBlackListTableChangePage(page) {
                this.$refs.pendingListTable.changePage(page);
            },
        }
    }
</script>





