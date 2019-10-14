<template>
<div>
  <b-row>
    <b-colxx xxs="12">
      <piaf-breadcrumb :heading="$t('menu.site-management')"/>
      <div class="separator mb-5"></div>
    </b-colxx>
  </b-row>

  <b-tabs nav-class="separator-tabs ml-0 mb-5" content-class="tab-content" :no-fade="true">
    <b-tab :title="$t('system-setting.site-list')">
      <b-row v-if="!detailMode">
        <b-col cols="12" class="mb-4">
          <b-card class="mb-4" no-body>
            <b-card-body>
              <b-row>
                <b-col class="d-flex">
                  <div class="flex-grow-1">

                    <b-row>

                      <b-col >
                        <b-form-group :label="$t('system-setting.site-name')">
                          <b-form-input></b-form-input>
                        </b-form-group>
                      </b-col>

                      <b-col >
                        <b-form-group :label="$t('system-setting.status')">
                          <v-select :options="stateOptions" v-model="selectedStatus" plain/>
                        </b-form-group>
                      </b-col>

                      <b-col >
                        <b-form-group :label="$t('system-setting.super-site-name')">
                          <b-form-input></b-form-input>
                        </b-form-group>
                      </b-col>
                      <b-col ></b-col>
                    </b-row>

                  </div>
                  <div class="align-self-center">
                    <b-button size="sm" class="ml-2" variant="info">{{ $t('system-setting.search') }}</b-button>
                    <b-button size="sm" class="ml-2" variant="info">{{ $t('system-setting.reset') }}</b-button>
                    <b-button size="sm" class="ml-2" variant="success" v-on:click="onNewClicked">{{ $t('system-setting.new') }}</b-button>
                    <b-button size="sm" class="ml-2" variant="outline-info">{{ $t('system-setting.export') }}</b-button>
                    <b-button size="sm" class="ml-2" variant="outline-info">{{ $t('system-setting.print') }}</b-button>
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
                    class="table-striped"
                  >
                    <div slot="operating" slot-scope="props">
                      <b-button v-if="props.rowData.status === 'active'" size="xs" variant="info" disabled>{{$t('system-setting.modify')}}</b-button>
                      <b-button v-if="props.rowData.status === 'active'" size="xs" variant="warning">{{$t('system-setting.status-inactive')}}</b-button>
                      <b-button v-if="props.rowData.status === 'active'" size="xs" variant="danger" disabled>{{$t('system-setting.delete')}}</b-button>

                      <b-button v-if="props.rowData.status === 'inactive'" size="xs" variant="info" @click="editRow(props.rowData)">{{$t('system-setting.modify')}}</b-button>
                      <b-button v-if="props.rowData.status === 'inactive'" size="xs" variant="success">{{$t('system-setting.status-active')}}</b-button>
                      <b-button v-if="props.rowData.status === 'inactive'" size="xs" variant="danger">{{$t('system-setting.delete')}}</b-button>
                    </div>
                  </vuetable>
                  <vuetable-pagination-bootstrap
                    ref="pagination"
                    @vuetable-pagination:change-page="onChangePage"
                  ></vuetable-pagination-bootstrap>
                </b-col>
              </b-row>
            </b-card-body>
          </b-card>
        </b-col>
      </b-row>
      <b-row v-if="detailMode">
        <b-colxx xxs="12">
          <b-card ref="detailsCard" class="mb-4">
            <b-form @submit.prevent="onHorizontalSubmit">
              <b-form-group label-cols="2" horizontal :label="$t('system-setting.site-name')">
                <b-form-input :placeholder="$t('system-setting.please-enter-site-name')"></b-form-input>
              </b-form-group>
              <b-form-group label-cols="2" horizontal :label="$t('system-setting.site-no')">
                <b-form-input :placeholder="$t('system-setting.please-enter-site-no')"></b-form-input>
              </b-form-group>
              <b-form-group label-cols="2" horizontal :label="$t('system-setting.super-site-name')">
                <v-select :options="superSiteOptions" v-model="selectedStatus"  plain  />
              </b-form-group>
              <b-form-group label-cols="2" horizontal :label="$t('system-setting.super-site-no')">
                <b-form-input :disabled="true" v-model="selectedStatus.value"></b-form-input>
              </b-form-group>
              <b-form-group label-cols="2" horizontal :label="$t('system-setting.manager')">
                <b-form-input :placeholder="$t('system-setting.please-enter-manager')"></b-form-input>
              </b-form-group>
              <b-form-group label-cols="2" horizontal :label="$t('system-setting.contact-info')">
                <b-form-input :placeholder="$t('system-setting.please-enter-manager-contact-info')"></b-form-input>
              </b-form-group>
              <b-form-group label-cols="2" horizontal :label="$t('system-setting.remarks')">
                <b-form-textarea rows="6" :placeholder="$t('system-setting.please-enter-remarks')"></b-form-textarea>
              </b-form-group>
              <b-form-group label-cols="2" horizontal>
                <b-button type="submit" variant="info" class="mt-4">{{ $t('system-setting.save') }}</b-button>
                <b-button variant="info" class="mt-4 ml-2" v-on:click="onReturnClicked">{{ $t('system-setting.return') }}</b-button>
              </b-form-group>
            </b-form>
          </b-card>
        </b-colxx>
      </b-row>
    </b-tab>

    <b-tab :title="$t('system-setting.site-architecture')">
      <b-row>
        <b-col cols="12">
          <b-card class="mb-4" no-body>
            <b-card-body>
            </b-card-body>
          </b-card>
        </b-col>
      </b-row>
    </b-tab>
  </b-tabs>
  </div>
</template>

<script>
  import _ from 'lodash';
  import InputTag from '../../../components/Form/InputTag';
  import vSelect from 'vue-select'
  import Vuetable from 'vuetable-2/src/components/Vuetable'
  import VuetablePagination from 'vuetable-2/src/components/VuetablePagination'
  import VuetablePaginationBootstrap from '../../../components/Common/VuetablePaginationBootstrap'
  import 'vue-select/dist/vue-select.css'

  export default {
      components: {
          'input-tag' : InputTag,
          'v-select' : vSelect,
          'vuetable' : Vuetable,
          'vuetable-pagination': VuetablePagination,
          'vuetable-pagination-bootstrap' : VuetablePaginationBootstrap
      },
      data () {
          return {
              selectedStatus: '',
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
                          name: 'site-no',
                          sortField: 'site-no',
                          title: this.$t('system-setting.site-no'),
                          titleClass: 'text-center',
                          dataClass: 'text-center'
                      },
                      {
                          name: 'site-name',
                          sortField: 'site-name',
                          title: this.$t('system-setting.site-name'),
                          titleClass: 'text-center',
                          dataClass: 'text-center'
                      },
                      {
                          name: 'status',
                          sortField: 'status',
                          title: this.$t('system-setting.status'),
                          titleClass: 'text-center',
                          dataClass: 'text-center',
                          callback: (value) => {
                              const dictionary = {
                                  "active": `<span class="text-success">${this.$t('system-setting.status-active')}</span>`,
                                  "inactive": `<span class="text-muted">${this.$t('system-setting.status-inactive')}</span>`
                              };
                              if(!dictionary.hasOwnProperty(value)) return '';
                              return dictionary[value];
                          }
                      },
                      {
                          name: 'super-site-no',
                          sortField: 'super-site-no',
                          title: this.$t('system-setting.super-site-no'),
                          titleClass: 'text-center',
                          dataClass: 'text-center',
                          callback: (value) => {
                              if(value) {
                                  return value;
                              } else {
                                  return this.$t('system-setting.none');
                              }
                          }
                      },
                      {
                          name: 'super-site-name',
                          sortField: 'super-site-name',
                          title: this.$t('system-setting.super-site-name'),
                          titleClass: 'text-center',
                          dataClass: 'text-center',
                          callback: (value) => {
                              if(value) {
                                  return value;
                              } else {
                                  return this.$t('system-setting.none');
                              }
                          }
                      },
                      {
                          name: 'manager',
                          sortField: 'manager',
                          title: this.$t('system-setting.manager'),
                          titleClass: 'text-center',
                          dataClass: 'text-center'
                      },
                      {
                          name: 'contact-info',
                          sortField: 'contact-info',
                          title: this.$t('system-setting.contact-info'),
                          titleClass: 'text-center',
                          dataClass: 'text-center'
                      },
                      {
                          name: 'remarks',
                          sortField: 'remarks',
                          title: this.$t('system-setting.remarks'),
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
                      "site-no": "0000",
                      "site-name": "首都机场",
                      "status": "active",
                      "super-site-no": null,
                      "super-site-name": null,
                      "manager": "张三",
                      "contact-info": "13800001234",
                      "remarks": "",
                  },
                  {
                      "no": 2,
                      "site-no": "0100",
                      "site-name": "1号航站楼",
                      "status": "active",
                      "super-site-no": "0000",
                      "super-site-name": "总部",
                      "manager": "",
                      "contact-info": "13800001234",
                      "remarks": "",
                  },
                  {
                      "no": 3,
                      "site-no": "0200",
                      "site-name": "2号航站楼",
                      "status": "active",
                      "super-site-no": "0000",
                      "super-site-name": "总部",
                      "manager": "",
                      "contact-info": "13800001234",
                      "remarks": "",
                  },
                  {
                      "no": 4,
                      "site-no": "0201",
                      "site-name": "通道1",
                      "status": "active",
                      "super-site-no": "0200",
                      "super-site-name": "生产部",
                      "manager": "",
                      "contact-info": "13800001234",
                      "remarks": "",
                  },
                  {
                      "no": 5,
                      "site-no": "0202",
                      "site-name": "通道2",
                      "status": "inactive",
                      "super-site-no": "0200",
                      "super-site-name": "生产部",
                      "manager": "",
                      "contact-info": "13800001234",
                      "remarks": "",
                  },
                  {
                      "no": 6,
                      "site-no": "0300",
                      "site-name": "3号航站楼",
                      "status": "active",
                      "super-site-no": "0000",
                      "super-site-name": "总部",
                      "manager": "",
                      "contact-info": "13800001234",
                      "remarks": "",
                  },
                  {
                      "no": 7,
                      "site-no": "0301",
                      "site-name": "通道001",
                      "status": "inactive",
                      "super-site-no": "0300",
                      "super-site-name": "销售部",
                      "manager": "",
                      "contact-info": "13800001234",
                      "remarks": "",
                  },
              ],
              stateOptions: [
                  {value: "all", label: this.$t('system-setting.status-all')},
                  {value: "valid", label: this.$t('system-setting.status-active')},
                  {value: "invalid", label: this.$t('system-setting.status-inactive')},
              ],
              detailMode: false,
              selectedSite: '0000',
              superSiteOptions: [
                  {value: "0000", label: '首都机场'},
                  {value: "0001", label: '1号航站楼'},
                  {value: "0002", label: '2号航站楼'},
                  {value: "0003", label: '通道1'},
                  {value: "0004", label: '通道2'},
                  {value: "0020", label: '3号航站楼'},
                  {value: "0030", label: '通道001'},
              ]
          }
      },
      methods: {
          onNewClicked() {
              this.detailMode = true;
          },
          onPaginationData(paginationData) {
              this.$refs.pagination.setPaginationData(paginationData);
          },
          onChangePage(page) {
              this.$refs.vuetable.changePage(page);
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
          editRow(data) {
              console.log(data);
              this.detailMode = true;
          },
          onReturnClicked() {
              this.detailMode = false;
          }
      }
  }
</script>
