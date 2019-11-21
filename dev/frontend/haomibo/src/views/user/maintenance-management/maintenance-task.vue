<style lang="scss">

  .maintenance-task {
    .form-section {
      .form-group {
        max-width: unset;

        .form-control {
          max-width: 400px;
        }
      }
    }

    .inline-form-no-margin {
      .form-group {
        margin-bottom: 0;

        label.input-label {
          line-height: unset;
        }
      }

      .col-form-label {
        padding-top: 0;
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

    div.maintenance-task-img-wrapper {
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
  <div class="maintenance-task">
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
          <b-col cols="8">
            <b-row>
              <b-col>
                <b-form-group :label="$t('maintenance-management.maintenance-task.task')">
                  <b-form-input v-model="filterForm.task"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group :label="$t('maintenance-management.maintenance-task.status')">
                  <b-form-select v-model="filterForm.status" :options="statusOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group :label="$t('maintenance-management.maintenance-task.position')">
                  <b-form-select v-model="filterForm.position" :options="positionOptions" plain/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group :label="$t('maintenance-management.maintenance-task.device-classification')">
                  <b-form-select v-model="filterForm.deviceClassification" :options="deviceClassificationOptions"
                                 plain/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group :label="$t('maintenance-management.maintenance-task.device')">
                  <b-form-input v-model="filterForm.device"></b-form-input>
                </b-form-group>
              </b-col>
            </b-row>
          </b-col>
          <b-col cols="4" class="d-flex justify-content-end align-items-center">
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
                      @click="onAction('show',props.rowData)">{{ props.rowData.number}}</span>
                </div>
                <div slot="operating" slot-scope="props">
                  <b-button
                    size="sm"
                    variant="success default btn-square"
                  >
                    <i class="icofont-check-circled"></i>
                  </b-button>
                  <b-button
                    size="sm"
                    variant="primary default btn-square"
                  >
                    <i class="icofont-recycle"></i>
                  </b-button>
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
        <b-tabs class="sub-tabs" nav-class="separator-tabs ml-0" content-class="tab-content"
                :no-fade="true">
          <b-tab :title="$t('maintenance-management.history-record.maintenance-info')">
            <b-row class="h-100 form-section">
              <b-col cols="8">
                <b-row>
                  <b-col>
                    <b-form-group>
                      <template slot="label">
                        {{$t('maintenance-management.history-record.number')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label class="text-primary">HR201909010001</label>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group>
                      <template slot="label">
                        {{$t('maintenance-management.history-record.task')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label>毫米波安检仪001巡检</label>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group>
                      <template slot="label">
                        {{$t('maintenance-management.history-record.site')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label>通道001</label>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col>
                    <b-form-group>
                      <template slot="label">
                        {{$t('maintenance-management.history-record.device-classification')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label>监管查验设备 / 人体查验设备</label>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group>
                      <template slot="label">
                        {{$t('maintenance-management.history-record.device')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label class="text-primary">A000</label>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group>
                      <template slot="label">
                        {{$t('maintenance-management.history-record.plan')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label>HR201909200001</label>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col>
                    <b-form-group>
                      <template slot="label">
                        {{$t('maintenance-management.history-record.security-department')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label>生产一部</label>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group>
                      <template slot="label">
                        {{$t('maintenance-management.history-record.security-user')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label>张三</label>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group>
                      <template slot="label">
                        {{$t('maintenance-management.history-record.creat-time')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label>20191022 00:00</label>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col>
                    <b-form-group>
                      <template slot="label">
                        {{$t('maintenance-management.history-record.estimated-start-time')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label>20191024 00:00</label>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group>
                      <template slot="label">
                        {{$t('maintenance-management.history-record.actual-start-time')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label>20191024 06:03</label>
                    </b-form-group>
                  </b-col>
                  <b-col>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col cols="8">
                    <b-form-group>
                      <template slot="label">
                        {{$t('maintenance-management.process-task.plan-works')}}&nbsp
                      </template>
                      <label>1.整体检查;</label>
                      <label>2.添加机油;</label>
                      <label>3.设备试运行</label>
                    </b-form-group>
                  </b-col>
                  <b-col>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col cols="8">
                    <b-form-group>
                      <template slot="label">
                        {{$t('maintenance-management.process-task.real-works')}}&nbsp
                      </template>
                      <b-form-textarea type="text" :placeholder="$t('maintenance-management.process-task.real-works')"/>
                    </b-form-group>
                  </b-col>
                  <b-col>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col cols="4">
                    <b-form-group>
                      <template slot="label">
                        {{$t('maintenance-management.process-task.real-works')}}&nbsp
                      </template>
                      <b-button class="mt-2" variant="info skyblue default" size="sm">
                        <i class="icofont-upload-alt mr-1"></i>{{$t('permission-management.upload-image')}}
                      </b-button>
                    </b-form-group>
                  </b-col>
                  <b-col>
                  </b-col>
                </b-row>
              </b-col>
              <b-col cols="4" class="d-flex flex-column align-items-center">
                <div class="maintenance-task-img-wrapper">
                  <img src="../../../assets/img/man-in-device.png">
                </div>
              </b-col>

            </b-row>
          </b-tab>
          <b-tab :title="$t('maintenance-management.history-record.device-info')">
            <b-row class="h-100 form-section inline-form-no-margin">
              <b-col cols="8">
                <b-row>
                  <b-col cols="4" class="mb-2">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('maintenance-management.history-record.device-no')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label class="input-label">A000</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4" class="mb-2">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('maintenance-management.history-record.device-name')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label class="input-label">MW毫米波安检仪000</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4" class="mb-2">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('maintenance-management.history-record.file-name')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label class="input-label">MW毫米波安检仪</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4" class="mb-2">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('maintenance-management.history-record.device-classification')}}&nbsp
                        <span class="text-danger">*</span>
                      </template>
                      <label class="input-label">监管查验设备 / 人体查验设备</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4" class="mb-2">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('maintenance-management.history-record.manufacturer')}}&nbsp
                      </template>
                      <label class="input-label">同方威视</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4" class="mb-2">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('maintenance-management.history-record.origianl-model')}}&nbsp
                      </template>
                      <label class="input-label">MW1000AA</label>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row class="mb-5">
                  <b-col cols="12" class="d-flex align-items-center">
                    <label class="pr-2 m-0 "
                           style="color: #bdbaba">{{$t('maintenance-management.history-record.device-info')}}</label>
                    <div class="flex-grow-1" style="height: 1px;background-color: #bdbaba"></div>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col cols="4" class="mb-2">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('maintenance-management.history-record.original-no')}}&nbsp
                      </template>
                      <label class="input-label">H20190002</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4" class="mb-2">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('maintenance-management.history-record.product-date')}}&nbsp
                      </template>
                      <label class="input-label">20190201</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4" class="mb-2">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('maintenance-management.history-record.purchase-date')}}&nbsp
                      </template>
                      <label class="input-label">20190201</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4" class="mb-2">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('maintenance-management.history-record.supplier')}}&nbsp
                      </template>
                      <label class="input-label">同方威视</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4" class="mb-2">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('maintenance-management.history-record.supplier-contact')}}&nbsp
                      </template>
                      <label class="input-label">小明</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4" class="mb-2">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('maintenance-management.history-record.supplier-contact-information')}}&nbsp
                      </template>
                      <label class="input-label">13800003001</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4" class="mb-2">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('maintenance-management.history-record.maintenance-department')}}&nbsp
                      </template>
                      <label class="input-label">生产一部</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4" class="mb-2">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('maintenance-management.history-record.maintenance-user')}}&nbsp
                      </template>
                      <label class="input-label">张三</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4" class="mb-2">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('maintenance-management.history-record.maintenance-contact-info')}}&nbsp
                      </template>
                      <label class="input-label">13800000002</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4" class="mb-2">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('maintenance-management.history-record.regular-task')}}&nbsp
                      </template>
                      <label class="input-label">任务1、任务2</label>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row class="mb-5">
                  <b-col cols="12" class="d-flex align-items-center">
                    <label class="pr-2 m-0 "
                           style="color: #bdbaba">{{$t('maintenance-management.history-record.technical-indicator')}}</label>
                    <div class="flex-grow-1" style="height: 1px;background-color: #bdbaba"></div>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col cols="4" class="mb-2">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('maintenance-management.history-record.inspection-method')}}&nbsp
                      </template>
                      <label class="input-label">非接触式</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4" class="mb-2">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('maintenance-management.history-record.single-scan-time')}}&nbsp
                      </template>
                      <label class="input-label">2</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4" class="mb-2">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('maintenance-management.history-record.number-of-operator')}}&nbsp
                      </template>
                      <label class="input-label">1</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4" class="mb-2">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('maintenance-management.history-record.detectable-item-type')}}&nbsp
                      </template>
                      <label class="input-label">金属，非金属，爆炸物，毒品，液体</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4" class="mb-2">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('maintenance-management.history-record.automatic-identification')}}&nbsp
                      </template>
                      <label class="input-label">有</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('maintenance-management.history-record.privacy-protection')}}&nbsp
                      </template>
                      <label class="input-label">有</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4" class="mb-2">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('maintenance-management.history-record.equipment-size')}}&nbsp
                      </template>
                      <label class="input-label">2400*1400*1706</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4" class="mb-2">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('maintenance-management.history-record.channel-size')}}&nbsp
                      </template>
                      <label class="input-label">2200*750*1188</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4" class="mb-2">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('maintenance-management.history-record.equipment-weight')}}&nbsp
                      </template>
                      <label class="input-label">550</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4" class="mb-2">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('maintenance-management.history-record.power-by')}}&nbsp
                      </template>
                      <label class="input-label">110/220，50/60</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4" class="mb-2">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('maintenance-management.history-record.rated-power')}}&nbsp
                      </template>
                      <label class="input-label">2200*750*1188</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4" class="mb-2">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('maintenance-management.history-record.operating-temperature-humidity')}}&nbsp
                      </template>
                      <label class="input-label">0-40，0-93</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="4" class="mb-2">
                    <b-form-group horizontal label-cols="5">
                      <template slot="label">
                        {{$t('maintenance-management.history-record.storage-temperature-humidity')}}&nbsp
                      </template>
                      <label class="input-label">-20-55，0-93</label>
                    </b-form-group>
                  </b-col>
                </b-row>
              </b-col>
              <b-col cols="4" class="d-flex flex-column align-items-center">
                <div class="maintenance-task-img-wrapper">
                  <img src="../../../assets/img/man-in-device.png">
                </div>
              </b-col>

            </b-row>
          </b-tab>
        </b-tabs>
        <div class="d-flex align-items-end justify-content-end flex-grow-1 mr-3 mb-3">
          <div>
            <b-button size="sm" variant="info default">
              <i class="icofont-save"></i> {{$t('device-management.save')}}
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
  import Vuetable from '../../../components/Vuetable2/Vuetable'
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
          status: null,
          position: null,
          deviceClassification: null,
          device: null
        },
        positionOptions: [
          {value: null, text: this.$t('permission-management.all')},
          {value: 'position-1', text: '清理'},
          {value: 'position-2', text: '维修'}
        ],
        statusOptions: [
          {value: null, text: this.$t('permission-management.all')},
          {value: 'completed', text: this.$t('maintenance-management.maintenance-task.completed')},
          {value: 'processing', text: this.$t('maintenance-management.maintenance-task.processing')},
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
              title: this.$t('maintenance-management.maintenance-task.no'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:number',
              sortField: 'number',
              title: this.$t('maintenance-management.maintenance-task.number'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'task',
              sortField: 'task',
              title: this.$t('maintenance-management.maintenance-task.task'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'status',
              sortField: 'status',
              title: this.$t('maintenance-management.maintenance-task.status'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (value) => {
                const dictionary = {
                  "completed": `<span class="text-primary">${this.$t('maintenance-management.maintenance-task.completed')}</span>`,
                  "processing": `<span class="text-warning">${this.$t('maintenance-management.maintenance-task.processing')}</span>`,
                };
                if (!dictionary.hasOwnProperty(value)) return '';
                return dictionary[value];
              }
            },
            {
              name: 'category',
              sortField: 'category',
              title: this.$t('maintenance-management.maintenance-task.category'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'position',
              sortField: 'position',
              title: this.$t('maintenance-management.maintenance-task.position'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'deviceClassification',
              sortField: 'deviceClassification',
              title: this.$t('maintenance-management.maintenance-task.device-classification'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'device',
              sortField: 'device',
              title: this.$t('maintenance-management.maintenance-task.device'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'plan',
              sortField: 'plan',
              title: this.$t('maintenance-management.maintenance-task.plan'),
              titleClass: 'text-center',
              dataClass: 'text-center'
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
        tempData: [
          {
            "no": 1,
            "number": "H201909200001",
            "status": "processing",
            "category": "清理",
          },
          {
            "no": 2,
            "number": "H201909200002",
            "status": "completed",
            "category": "清理",
          },
          {
            "no": 3,
            "number": "H201909200003",
            "status": "processing",
          },
          {
            "no": 4,
            "number": "H201909200004",
            "status": "processing",
          },
        ]
      }
    },
    methods: {
      onSearchButton() {
      },
      onResetButton() {
      },
      onAction(value) {
        switch (value) {
          case 'show-list':
            this.pageStatus = 'list';
            break;
          case 'show':
            this.pageStatus = 'show';
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





