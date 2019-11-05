<template>
  <div>
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb />
        </b-colxx>
      </b-row>
    </div>
    <b-card>
      <div v-if="pageStatus==='list'">
        <b-row>
          <b-col class="d-flex">
            <div class="flex-grow-1">

              <b-row>
                <b-col>
                  <b-form-group :label="$t('device-management.file-name')">
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
                <b-col></b-col>
              </b-row>

            </div>
            <div class="align-self-center">
              <b-button size="sm" class="ml-2" variant="info default">
                <i class="icofont-search-1"></i>&nbsp;{{ $t('permission-management.search') }}
              </b-button>
              <b-button size="sm" class="ml-2" variant="info default">
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
        <b-row>
          <b-col>
            <vuetable
              ref="vuetable"
              :api-mode="false"
              :fields="vuetableItems.fields"
              :data-manager="dataManager"
              :per-page="vuetableItems.perPage"
              pagination-path="pagination"
              @vuetable:pagination-data="onPaginationData"
              class="table-striped text-center"
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
            <vuetable-pagination-bootstrap
              ref="pagination"
              @vuetable-pagination:change-page="onChangePage"
            ></vuetable-pagination-bootstrap>
          </b-col>
        </b-row>
      </div>
      <div v-if="pageStatus==='create'" class="form-section">
        <b-row>
          <b-col xxs="12" md="4" lg="4">
                <b-form @submit.prevent="onBasicSubmit">
                  <b-form-group :label="$t('device-management.template-number')">
                    <b-form-input type="text" v-model="basicForm.templateNumber"
                                  :placeholder="$t('device-management.template-number-placeholder')"/>
                  </b-form-group>
                  <b-form-group :label="$t('device-management.template-name')">
                    <b-form-input type="text" v-model="basicForm.templateName"
                                  :placeholder="$t('device-management.template-name-placeholder')"/>
                  </b-form-group>
                  <b-form-group :label="$t('device-management.device-classify')">
                    <b-form-select v-model="basicForm.deviceClassify" :options="deviceClassifyData" :placeholder="$t('device-management.device-classify-placeholder')" plain/>
                  </b-form-group>
                  <b-form-group :label="$t('device-management.manufacture')">
                    <b-form-select v-model="basicForm.manufacture" :options="selectData" plain />
                  </b-form-group>
                  <b-form-group :label="$t('device-management.origin-model')">
                    <b-form-input type="text" v-model="basicForm.originModel"
                                  :placeholder="$t('device-management.origin-model-placeholder')"/>
                  </b-form-group>
                </b-form>
          </b-col>
          <b-col xxs="12" md="8" lg="8">
                <b-row>
                  <b-col cols="12" class="mb-2 d-flex justify-content-between align-items-baseline"><label class="font-weight-bold">{{$t('device-management.document-template.device-show-list')}}</label> <b-button size="sm" variant="success default">
                    <i class="icofont-plus"></i> {{$t('device-management.document-template.add-indicator')}}</b-button></b-col>
                  <b-col cols="12" class="table-responsive text-center">

                    <vuetable
                      ref="deviceShowingTable"
                      :api-mode="false"
                      :fields="deviceShowingTableItems.fields"
                      :data-manager="deviceShowingTableDataManager"
                      :per-page="deviceShowingTableItems.perPage"
                      pagination-path="deviceShowingTablePagination"
                      @vuetable:pagination-data="onDeviceShowingTablePaginationData"
                      class="table-striped text-center"
                    >
                      <div slot="number" slot-scope="props">
                        <span class="cursor-p text-primary" >{{ props.rowData.number }}</span>
                      </div>
                      <div slot="required" slot-scope="props">
                        <b-button v-if="props.rowData.status"
                          size="xs"
                          variant="success default" >
                          <i class="icofont-check-alt"></i>&nbsp;{{$t('device-management.document-template.yes')}}
                        </b-button>
                        <b-button v-if="!props.rowData.status"
                                  size="xs"
                                  variant="light default" >
                          <i class="icofont-close-line"></i>&nbsp;{{$t('device-management.document-template.no')}}
                        </b-button>
                      </div>
                      <div slot="action" slot-scope="props">
                        <b-button
                          size="sm"
                          variant="danger default btn-square" >
                          <i class="icofont-bin"></i>
                        </b-button>
                      </div>
                    </vuetable>
                    <vuetable-pagination-bootstrap
                      ref="deviceShowingTablePagination"
                      @vuetable-pagination:change-page="onDeviceShowingTableChangePage"
                    ></vuetable-pagination-bootstrap>
                  </b-col>
                </b-row>
          </b-col>
          <b-col cols="12 text-right mt-3">
            <b-button size="sm" variant="info default"><i class="icofont-save"></i> {{$t('device-management.save')}}</b-button>
            <b-button size="sm" variant="success default"><i class="icofont-check-circled"></i> {{$t('device-management.active')}}</b-button>
            <b-button size="sm" variant="danger default"><i class="icofont-bin"></i> {{$t('device-management.delete')}}</b-button>
            <b-button size="sm" variant="info default" @click="onAction('show-list')"><i class="icofont-long-arrow-left"></i> {{$t('device-management.return')}}</b-button>
          </b-col>
        </b-row>
      </div>
    </b-card>
  </div>
</template>
<script>
  import _ from 'lodash';
  import {apiBaseUrl} from '../../../constants/config'
  import Vuetable from 'vuetable-2/src/components/Vuetable'
  import VuetablePaginationBootstrap from '../../../components/Common/VuetablePaginationBootstrap'
  export default {
    components: {
      'vuetable': Vuetable,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
    },
    data() {
      return {
        pageStatus:'list',
        searchType: '全部',
        typeData: [
          {label: this.$t('device-management.all'), value: ''},
          {label: this.$t('device-management.active'), value: 'Active'},
          {label: this.$t('device-management.inactive'), value: 'Inactive'}
        ],
        stateOptions: {
          'all':this.$t('system-setting.status-all'),
          'valid':this.$t('system-setting.status-active'),
          'invalid':this.$t('system-setting.status-inactive')
        },
        basicForm: {
          templateName: '',
          templateNumber: '',
          deviceClassify: '',
          manufacture: '',
          originModel: '',
        },
        deviceClassifyData: [
          {text: "人体查验设备", value: 'chocolate'},
          {text: "物品查验设备", value: 'vanilla'},
          {text: "车辆查验设备", value: 'vanilla'}
        ],
        selectData: [
          {text: "同方威视", value: 'chocolate'},
          {text: "海康威视", value: 'chocolate'}
        ],
        vuetableItems: {
          perPage: 5,
          fields: [
            {
              name: 'no',
              sortField: 'no',
              title: this.$t('system-setting.no'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:number',
              sortField: 'number',
              title: this.$t('device-management.file-no'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'filename',
              sortField: 'filename',
              title: this.$t('device-management.file-name'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'setting',
              sortField: 'setting',
              title: this.$t('device-management.setting'),
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
              title: this.$t('device-management.origin-model'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:operating',
              title: this.$t('system-setting.operating'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            }
          ]
        },
        tempData: [
          {
            "no": 1,
            "number": "0000",
            "file-name": "首都机场",
            "setting": null,
            "status": "active",
            "classify": null,
            "manufacturer": "张三",
            "origin-no": "13800001234",
            "remarks": "",
          },
          {
            "no": 2,
            "number": "0001",
            "file-name": "首都机场",
            "setting": null,
            "status": "active",
            "classify": null,
            "manufacturer": "张三",
            "origin-no": "13800001234",
            "remarks": "",
          },
          {
            "no": 3,
            "file-no": "0002",
            "file-name": "首都机场",
            "setting": null,
            "status": "active",
            "classify": null,
            "manufacturer": "张三",
            "origin-no": "13800001234",
            "remarks": "",
          },
          {
            "no": 4,
            "file-no": "0004",
            "file-name": "首都机场",
            "setting": null,
            "status": "active",
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
            "filename": null,
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
            "filename": null,
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
            "filename": null,
            "classify": null,
            "manufacturer": "张三",
            "origin-no": "13800001234",
            "remarks": "",
          },
        ],
        deviceShowingTableItems: {
          apiUrl: apiBaseUrl + '/cakes/fordatatable',
          fields: [
            {
              name: 'no',
              sortField: 'no',
              title: this.$t('device-management.no'),
              titleClass: 'text-center',
              dataClass: 'list-item-heading'
            },
            {
              name: '__slot:number',
              sortField: 'number',
              title: this.$t('device-management.document-template.indicator'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'name',
              sortField: 'name',
              title: this.$t('device-management.document-template.classify'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'classify',
              sortField: 'classify',
              title: this.$t('device-management.document-template.unit'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:required',
              sortField: 'setting',
              title: this.$t('device-management.document-template.required'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:action',
              title: this.$t('device-management.action'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            }
          ],
          perPage:5
        },
        tempData2: [
          {
            "no": 1,
            "number": "0000",
            "name": "首都机场",
            "status":true,
            "classify": "张三",
          },
          {
            "no": 2,
            "number": "0000",
            "name": "首都机场",
            "status":true,
            "classify": "张三",
          },
          {
            "no": 3,
            "number": "0000",
            "name": "首都机场",
            "status":false,
            "classify": "张三",
          },
          {
            "no": 4,
            "number": "0000",
            "name": "首都机场",
            "status":true,
            "classify": "张三",
          },
          {
            "no": 5,
            "number": "0000",
            "name": "首都机场",
            "status":true,
            "classify": "张三",
          },
          {
            "no": 6,
            "number": "0003",
            "name": "首都机场",
            "status":true,
            "classify": "张三",
          },
          {
            "no": 7,
            "number": "0005",
            "name": "首都机场",
            "status":true,
            "classify": "张三",
          },
        ],
      }
    },
    methods: {
      onSearchButton(){

      },
      onResetButton(){

      },
      onAction(value) {
        switch (value) {
          case 'create':
            this.pageStatus = 'create';
            break;
          case 'edit':
            this.pageStatus = 'create';
            break;
          case 'show-list':
            this.pageStatus = 'list';
            break;
        }
      },
      onBasicSubmit() {
        console.log(JSON.stringify(this.basicForm))
      },
      onPaginationData(paginationData) {
        this.$refs.pagination.setPaginationData(paginationData)
      },
      onChangePage(page) {
        this.$refs.vuetable.changePage(page)
      },
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
      },
      //todo for deviceShowingTable points.
      onDeviceShowingTablePaginationData(paginationData) {
        this.$refs.deviceShowingTablePagination.setPaginationData(paginationData)
      },
      onDeviceShowingTableChangePage(page) {
        this.$refs.deviceShowingTable.changePage(page)
      },
      deviceShowingTableDataManager(sortOrder, pagination) {
        let local = this.tempData2;

        // sortOrder can be empty, so we have to check for that as well
        if (sortOrder.length > 0) {
          local = _.orderBy(
            local,
            sortOrder[0].sortField,
            sortOrder[0].direction
          );
        }
        pagination = this.$refs.deviceShowingTable.makePagination(
          local.length,
          this.deviceShowingTableItems.perPage
        );

        let from = pagination.from - 1;
        let to = from + this.deviceShowingTableItems.perPage;

        return {
          pagination: pagination,
          data: _.slice(local, from, to)
        };
      }

    }
  }
</script>
