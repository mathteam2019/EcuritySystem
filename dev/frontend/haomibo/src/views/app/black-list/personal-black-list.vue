<template>
  <div>
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb />
        </b-colxx>
      </b-row>
    </div>
    <b-tabs nav-class="ml-2" :no-fade="true">

      <b-tab :title="$t('black-list.personal-black-list')">
        <div v-if="pageStatus==='list'">
          <b-row>
            <b-col cols="6">
              <b-row>
                <b-col>
                  <b-form-group :label="$t('black-list.user-name')">
                    <b-form-input v-model="filterForm.userName"></b-form-input>
                  </b-form-group>
                </b-col>
                <b-col>
                  <b-form-group :label="$t('black-list.license-number')">
                    <b-form-input v-model="filterForm.licenseNumber"></b-form-input>
                  </b-form-group>
                </b-col>
                <b-col>
                  <b-form-group :label="$t('black-list.grade')">
                    <b-form-select v-model="filterForm.grade" :options="gradeOptions" plain/>
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
          <b-row>
            <b-col>
              <vuetable
                ref="blackListTable"
                :api-mode="false"
                :fields="blackListTableItems.fields"
                :data-manager="blackListTableDataManager"
                :per-page="blackListTableItems.perPage"
                pagination-path="pagination"
                @vuetable:pagination-data="onBlackListTablePaginationData"
                class="table-striped"
              >
                <div slot="number" slot-scope="props">
                  <span class="cursor-p text-primary" @click="onAction('show',props.rowData)">{{ props.rowData.number}}</span>
                </div>
                <div slot="operating" slot-scope="props">
                  <b-button
                    size="sm"
                    variant="danger default btn-square">
                    <i class="icofont-bin"></i>
                  </b-button>
                </div>
              </vuetable>
              <vuetable-pagination-bootstrap
                ref="blackListTablePagination"
                :initial-per-page="blackListTableItems.perPage"
                @vuetable-pagination:change-page="onBlackListTableChangePage"
              ></vuetable-pagination-bootstrap>
            </b-col>
          </b-row>
        </div>
        <div v-if="pageStatus==='create'" class="form-section">

        </div>
      </b-tab>
      <b-tab :title="$t('black-list.statics')">

      </b-tab>
    </b-tabs>

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
    data(){
      return {
        pageStatus:'list',
        filterForm:{
          userName:null,
          licenseNumber:null,
          grade:null
        },
        gradeOptions:[
          '全部',
          '一等级',
          '二等级',
          '三等级'
        ],
        blackListTableItems: {
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
              title: this.$t('black-list.number'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'userName',
              sortField: 'userName',
              title: this.$t('black-list.user-name'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'gender',
              sortField: 'gender',
              title: this.$t('black-list.gender'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (value) => {
                const dictionary = {
                  "male": `<span>${this.$t('permission-management.male')}</span>`,
                  "female": `<span>${this.$t('permission-management.female')}</span>`,
                  "unknown": `<span>${this.$t('permission-management.unknown')}</span>`,
                };
                if (!dictionary.hasOwnProperty(value)) return '';
                return dictionary[value];
              }
            },
            {
              name: 'country',
              sortField: 'country',
              title: this.$t('black-list.country'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'licenseType',
              sortField: 'licenseType',
              title: this.$t('black-list.license-type'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'licenseNumber',
              sortField: 'licenseNumber',
              title: this.$t('black-list.license-number'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'birthday',
              sortField: 'birthday',
              title: this.$t('black-list.date-of-birthday'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'grade',
              sortField: 'grade',
              title: this.$t('black-list.grade'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'countSeized',
              sortField: 'countSeized',
              title: this.$t('black-list.count-of-seized'),
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
            "userName": "张三",
            "gender": "male",
            "country": null,
            "licenseNumber": "2139910831",
            "classify": null,
            "licenseType": "MW毫米波安检仪",
            "birthday":"2000-01-01",
            "grade": "",
            "countSeized": "7",
          },
          {
              "no": 2,
              "number": "0010",
              "userName": "美成",
              "gender": "female",
              "country": null,
              "licenseNumber": "1231231223",
              "classify": null,
              "licenseType": "华为M6平板",
              "birthday":"2000-01-01",
              "grade": "",
              "countSeized": "3",
          },
          {
              "no": 3,
              "number": "0011",
              "userName": "张三",
              "gender": "female",
              "country": null,
              "licenseNumber": "5643452345",
              "classify": null,
              "licenseType": "华为M6平板",
              "birthday":"2000-01-01",
              "grade": "",
              "countSeized": "3",
          },
          {
              "no": 4,
              "number": "0004",
              "userName": "张三",
              "gender": "female",
              "country": null,
              "licenseNumber": "7667345235",
              "classify": null,
              "licenseType": "华为M6平板",
              "birthday":"2000-01-01",
              "grade": "",
              "countSeized": "3",
          },
          {
              "no": 5,
              "number": "0111",
              "userName": "张三",
              "gender": "female",
              "country": null,
              "licenseNumber": "2337686856",
              "classify": null,
              "licenseType": "华为M6平板",
              "birthday":"2000-01-01",
              "grade": "",
              "countSeized": "3",
          },
          {
              "no": 6,
              "number": "0015",
              "userName": "美成",
              "gender": "female",
              "country": null,
              "licenseNumber": "1231231223",
              "classify": null,
              "licenseType": "华为M6平板",
              "birthday":"2000-01-01",
              "grade": "",
              "countSeized": "3",
          },
          {
              "no": 7,
              "number": "0610",
              "userName": "美成",
              "gender": "female",
              "country": null,
              "licenseNumber": "1231231223",
              "classify": null,
              "licenseType": "华为M6平板",
              "birthday":"2000-01-01",
              "grade": "",
              "countSeized": "3",
          },
        ],
      }

    },
    methods: {

      onSearchButton(){

      },
      onResetButton(){

      },
      onAction(value,data = null){
        return ;
        switch (value) {
          case 'create':
            this.pageStatus = 'create';
            break;
          case 'show':
            this.pageStatus = 'show';
            break;
          case 'show-list':
            this.pageStatus = 'list';
            break;
        }
      },
      blackListTableDataManager(sortOrder, pagination) {
        let local = this.tempData;

        // sortOrder can be empty, so we have to check for that as well
        if (sortOrder.length > 0) {
          local = _.orderBy(
            local,
            sortOrder[0].sortField,
            sortOrder[0].direction
          );
        }

        pagination = this.$refs.blackListTable.makePagination(
          local.length,
          this.blackListTableItems.perPage
        );

        let from = pagination.from - 1;
        let to = from + this.blackListTableItems.perPage;
        return {
          pagination: pagination,
          data: _.slice(local, from, to)
        };
      },
      onBlackListTablePaginationData(paginationData) {
        this.$refs.blackListTablePagination.setPaginationData(paginationData);
      },
      onBlackListTableChangePage(page) {
        this.$refs.blackListTable.changePage(page);
      },
    }
  }
</script>


