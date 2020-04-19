<style lang="scss">
  .item-wrapper {
    position: relative;
    height: fit-content !important;
    //padding-left: $item-padding;
    display: inline-block;
    width: 100%;

    //height: 100%;
    /*& > .item {*/
    /*  z-index: 1;*/
    /*  position: relative;*/
    /*  height: 100%;*/
    /*  width: 100%;*/
    /*  display: inline-block;*/
    /*  cursor: pointer;*/
    /*  !*&:hover {*!*/
    /*  !*  box-shadow: 1px 2px 0 #c6c6c6;*!*/
    /*  !*}*!*/
    /*  !*&.active {*!*/
    /*  !*  .item-header {*!*/
    /*  !*    border-bottom-color: #009900;*!*/
    /*  !*  }*!*/
    /*  !*}*!*/
    /*  .item-header {*/
    /*    background: #f3f3f3;*/
    /*    border-bottom: solid 2px #c6c6c6;*/
    /*    height: calculateRem(50px);*/
    /*    display: flex;*/
    /*    justify-content: space-between;*/
    /*    align-items: center;*/
    /*    padding: 0 calculateRem(20px) 0 calculateRem(20px);*/
    /*    .label {*/
    /*      white-space: pre;*/
    /*      font-size: calculateRem(15px);*/
    /*      color: #666666;*/
    /*      max-width: 100%;*/
    /*      flex-grow: 1;*/
    /*      text-overflow: ellipsis;*/
    /*      overflow: hidden;*/
    /*    }*/
    /*    .action-list {*/
    /*      white-space: pre;*/
    /*      img {*/
    /*        width: calculateRem(20px);*/
    /*        margin-left: 0.5rem;*/
    /*        &.disabled {*/
    /*          filter: grayscale(1);*/
    /*        }*/
    /*        img:first-child {*/
    /*          margin-left: 0;*/
    /*        }*/
    /*      }*/

    /*    }*/
    /*  }*/
    /*  .item-body {*/
    /*    padding: calculateRem(10px);*/
    /*    .left-side {*/
    /*      .action {*/
    /*        button.btn {*/
    /*          margin-bottom: calculateRem(10px);*/
    /*          white-space: pre;*/
    /*          font-size: calculateRem(11px);*/
    /*          &.btn-success {*/
    /*            background-color: #49cf6f;*/
    /*            border-color: #49cf6f;*/
    /*            &:hover {*/
    /*              background-color: darken(#49cf6f, 8%);*/
    /*              border-color: darken(#49cf6f, 8%);*/
    /*            }*/
    /*          }*/
    /*          &.btn-info {*/
    /*            background-color: #1782d4;*/
    /*            &:hover {*/
    /*              background-color: darken(#1782d4, 8%);*/
    /*              border-color: darken(#1782d4, 8%);*/
    /*            }*/
    /*          }*/
    /*        }*/
    /*      }*/
    /*      .img {*/
    /*        flex-grow: 1;*/
    /*        width: 65px;*/
    /*        height: 94px;*/
    /*        display: flex;*/
    /*        align-items: center;*/
    /*        img {*/
    /*          width: 90%;*/
    /*          object-fit: contain;*/
    /*        }*/
    /*      }*/
    /*    }*/
    /*    .right-side {*/
    /*      .text-top {*/
    /*        color: #1782d4;*/
    /*        font-weight: bold;*/
    /*        margin-bottom: calculateRem(15px);*/
    /*      }*/
    /*      .content {*/
    /*        & > div {*/
    /*          display: flex;*/
    /*          label {*/
    /*            white-space: pre;*/
    /*            overflow: visible;*/
    /*            text-overflow: ellipsis;*/
    /*            max-width: 100%;*/
    /*            color: #606266;*/
    /*            font-size: calculateRem(12px);*/
    /*            line-height: calculateRem(12px);*/
    /*            &:first-child {*/
    /*              width: 37%;*/
    /*              min-width: 37%;*/
    /*            }*/
    /*            &:last-child {*/
    /*              flex-grow: 1;*/
    /*            }*/
    /*            &.disabled {*/
    /*              color: #c0c0c0;*/
    /*            }*/
    /*          }*/
    /*        }*/

    /*      }*/

    /*      .caption {*/
    /*        width: 37%;*/
    /*      }*/
    /*    }*/
    /*  }*/
    /*}*/
    & > .item-extra-info {
      overflow-wrap: break-word;
      padding: calculateRem(18px);
      opacity: 0;
      transition: 0ms;
      display: none !important;
      border-radius: 0.3rem;
      position: absolute;
      top: 0;
      width: 100%;
      left: calculateRem(30px);
      background: wheat;
      z-index: 1;
      /*& > div {*/
      /*  & > div {*/
      /*    margin-bottom: calculateRem(4px);*/
      /*    align-items: center;*/
      /*    &:first-child {*/
      /*      width: calculateRem(75px);*/
      /*      margin-bottom: 0;*/
      /*      font-size: 0.7rem;*/
      /*      color: white;*/
      /*      white-space: pre;*/
      /*      overflow: hidden;*/
      /*      text-overflow: ellipsis;*/
      /*    }*/
      /*    &:last-child {*/
      /*      display: flex;*/
      /*      align-items: center;*/
      /*      flex-grow: 1;*/
      /*      color: white;*/
      /*      white-space: pre;*/
      /*      overflow: hidden;*/
      /*      text-overflow: ellipsis;*/
      /*      img {*/
      /*        width: calculateRem(12px);*/
      /*      }*/
      /*      span {*/
      /*        font-size: 0.7rem;*/
      /*        &.success {*/
      /*          color: #42b662;*/
      /*        }*/
      /*        &.pending {*/
      /*          color: #bbbbbb;*/
      /*        }*/
      /*        &.danger {*/
      /*          color: #e12c48;*/
      /*        }*/
      /*        margin-left: calculateRem(5px);*/
      /*        &.without {*/
      /*          margin-left: calculateRem(18px);*/
      /*        }*/
      /*      }*/
      /*      .chart-container {*/
      /*        width: 100%;*/
      /*        height: 100%;*/
      /*      }*/
      /*    }*/
      /*  }*/
      /*}*/
    }
    /*&.slide-left {*/
    /*  & > .item-extra-info {*/
    /*    left: 0;*/
    /*  }*/
    /*  &:hover {*/
    /*    & > .item-extra-info {*/
    /*      left: calc(1.25rem - 100%);*/
    /*    }*/

    /*  }*/
    /*}*/
    &:hover {
      & > .item {
        z-index: 4;
      }
      & > .item-extra-info {
        top: -0.5rem;
        padding: 0.5rem;
        //position: fixed;
        display: inline-block !important;
        opacity: 1;
        transition: 10ms;
        left: 100%;
        z-index: 1;
      }

    }
  }
  .col-form-label {
    margin-bottom: 1px;
  }
  .bg-organization-structure {
    background: url("../../../assets/img/bg-china-map.png") no-repeat center;
    background-size: contain;
    height: calc(100% - 50px);

    & > div {
      background: transparent !important;
    }

  }

  .img-rotate {
    -ms-transform: rotate(-15deg); /* IE 9 */
    -webkit-transform: rotate(-15deg); /* Safari 3-8 */
    transform: rotate(-15deg);
  }
</style>

<template>
  <div>
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb :heading="$t('menu.organization-management')"/>
          <div class="separator mb-5"></div>
        </b-colxx>
      </b-row>
    </div>


    <b-tabs v-show="!isLoading" nav-class="ml-2" :no-fade="true" class="h-100">

      <b-tab :title="$t('permission-management.organization-table')">
        <b-row v-show="pageStatus==='table'" class="h-100">
          <b-col cols="12 d-flex flex-column" style="padding-right: 15px; padding-left: 15px;">
            <b-row class="pt-2">
              <b-col class="d-flex">
                <div class="flex-grow-1">

                  <b-row>

                    <b-col>
                      <b-form-group :label="$t('permission-management.organization-name')">
                        <b-form-input v-model="filter.orgName"/>
                      </b-form-group>
                    </b-col>

                    <b-col>
                      <b-form-group :label="$t('permission-management.active-state')">
                        <b-form-select :options="statusSelectOptions" v-model="filter.status" plain/>
                      </b-form-group>
                    </b-col>

                    <b-col>
                      <b-form-group :label="$t('permission-management.parent-organization-name')">
                        <b-form-select :options="parentOrganizationNameFilterOptions"  v-model="filter.parentOrgId" plain/>
                      </b-form-group>
                    </b-col>

                    <b-col/>
                  </b-row>

                </div>
                <div class="align-self-center">
                  <b-button
                    size="sm"
                    class="ml-2"
                    variant="info default"
                    @click="onSearchButton()"><i class="icofont-search-1"/>&nbsp;
                    {{ $t('permission-management.search') }}
                  </b-button>
                  <b-button
                    size="sm"
                    class="ml-2"
                    variant="info default"
                    @click="onResetButton()"><i class="icofont-ui-reply"/>&nbsp;
                    {{ $t('permission-management.reset') }}
                  </b-button>
                  <b-button @click="onExportButton()" size="sm" class="ml-2" variant="outline-info default"
                            :disabled="checkPermItem('org_export')">
                    <i class="icofont-share-alt"/>&nbsp;{{ $t('permission-management.export') }}
                  </b-button>
                  <b-button @click="onPrintButton()" size="sm" class="ml-2" variant="outline-info default"
                            :disabled="checkPermItem('org_print')"><i class="icofont-printer"/>&nbsp;
                    {{ $t('permission-management.print') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="success default" @click="showCreatePage()"
                            :disabled="checkPermItem('org_create')">
                    <i class="icofont-plus"/>&nbsp;{{$t('permission-management.new') }}
                  </b-button>
                </div>
              </b-col>
            </b-row>
            <b-row class="flex-grow-1">
              <b-col cols="12">
                <div class="table-wrapper table-responsive">
                  <vuetable
                    ref="vuetable"
                    :api-url="vuetableItems.apiUrl"
                    :fields="vuetableItems.fields"
                    :http-fetch="vuetableHttpFetch"
                    :per-page="vuetableItems.perPage"
                    pagination-path="pagination"
                    class="table-striped"
                    track-by="orgId"
                    @vuetable:checkbox-toggled="onCheckStatusChange"
                    @vuetable:pagination-data="onPaginationData"
                  >
                    <template slot="orgNumberLabel" slot-scope="props">
                      <div v-if="!props.rowData.orgNumberLabel.isLong">
                        <span class="cursor-p text-primary" @click="onAction('show', props.rowData)">{{ props.rowData.orgNumberLabel.groupMember }}</span>
                      </div>
                      <div v-else>
                        <div class="item-wrapper slide-right">
                          <span class="item d-flex flex-column cursor-p text-primary" @click="onAction('show', props.rowData)">{{ props.rowData.orgNumberLabel.label }}</span>
                          <div class="item-extra-info flex-column d-flex">{{ props.rowData.orgNumberLabel.groupMember }}</div>
                        </div>
                      </div>
                    </template>
                    <template slot="actions" slot-scope="props">
                      <div>
                        <b-button
                          size="sm"
                          variant="info default btn-square"
                          :disabled="checkPermItem('org_modify')"
                          @click="onAction('modify', props.rowData, props.rowIndex)">
                          <i class="icofont-edit"/>
                        </b-button>
                        <b-button
                          size="sm"
                          v-if="props.rowData.status==='1000000702'"
                          variant="success default btn-square"
                          :disabled="checkPermItem('org_update_status')"
                          @click="onAction('activate', props.rowData, props.rowIndex)">
                          <i class="icofont-check-circled"/>
                        </b-button>
                        <b-button
                          size="sm"
                          v-if="props.rowData.status==='1000000701'"
                          variant="warning default btn-square"
                          :disabled="checkPermItem('org_update_status')"
                          @click="onAction('deactivate', props.rowData, props.rowIndex)">
                          <i class="icofont-ban"/>
                        </b-button>
                        <b-button
                          size="sm"
                          variant="danger default btn-square"
                          :disabled="checkPermItem('org_delete') || props.rowData.status==='1000000701'"
                          @click="onAction('delete', props.rowData, props.rowIndex)">
                          <i class="icofont-bin"/>
                        </b-button>
                      </div>
                    </template>
                  </vuetable>
                </div>
                <div class="pagination-wrapper">
                  <vuetable-pagination-bootstrap
                    ref="pagination"
                    @vuetable-pagination:change-page="onChangePage"
                    :initial-per-page="vuetableItems.perPage"
                    @onUpdatePerPage="vuetableItems.perPage = Number($event)"
                  />
                </div>
              </b-col>
            </b-row>
          </b-col>
        </b-row>
        <b-row v-if="pageStatus==='create'" class="h-100 form-section">
          <b-col cols="6">
            <b-row>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.organization-number')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-input type="text"
                                v-model="createPage.orgNumber"
                                :state="!$v.createPage.orgNumber.$dirty ? null : !$v.createPage.orgNumber.$invalid"
                                :placeholder="$t('permission-management.please-enter-organization-number')"/>
                  <b-form-invalid-feedback>
                    {{ $t('permission-management.organization-management.please-enter-organization-number') }}
                  </b-form-invalid-feedback>
                </b-form-group>
              </b-col>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.organization-name')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-input type="text"
                                v-model="createPage.orgName"
                                :state="!$v.createPage.orgName.$dirty ? null : !$v.createPage.orgName.$invalid"
                                :placeholder="$t('permission-management.please-enter-organization-name')"/>
                  <b-form-invalid-feedback>
                    {{ $t('permission-management.organization-management.please-enter-organization-name') }}
                  </b-form-invalid-feedback>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.parent-organization-number')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-input type="text" disabled v-model="createPageSelectedParentOrganizationNumber"
                                :placeholder="$t('permission-management.please-select-parent-organization')"/>
                </b-form-group>
              </b-col>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.parent-organization-name')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-select :options="parentOrganizationNameSelectOptions"
                                 :state="!$v.createPage.parentOrgId.$dirty ? null : !$v.createPage.parentOrgId.$invalid"
                                 v-model="createPage.parentOrgId" plain/>
                  <b-form-invalid-feedback>
                    {{ $t('permission-management.organization-management.please-select-parent-organization') }}
                  </b-form-invalid-feedback>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.organization-leader')}}</template>
                  <b-form-input type="text"
                                v-model="createPage.leader"
                                :placeholder="$t('permission-management.please-enter-organization-leader')"/>
                </b-form-group>
              </b-col>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.organization-mobile')}}</template>
                  <b-form-input type="text"
                                v-model="createPage.mobile"/>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.organization-note')}}</template>

                  <b-form-textarea
                    v-model="createPage.note"
                    :placeholder="$t('permission-management.please-enter-organization-note')"
                  />

                </b-form-group>
              </b-col>
            </b-row>
          </b-col>
          <b-col cols="12" class="d-flex justify-content-end align-self-end">
            <b-button size="sm" variant="success default mr-1" @click="onCreatePageSaveButton()"><i
              class="icofont-save"/> {{
              $t('permission-management.save-button') }}
            </b-button>
            <b-button size="sm" variant="primary default " @click="onCreatePageBackButton()"><i
              class="icofont-long-arrow-left"/> {{
              $t('permission-management.back-button') }}
            </b-button>
          </b-col>
          <div v-if="getLocale()==='zh'" class="position-absolute" style="left: 32%;bottom: 8%">
            <img src="../../../assets/img/no_active_stamp.png">
          </div>
          <div v-if="getLocale()==='en'" class="position-absolute" style="left: 32%;bottom: 8%">
            <img src="../../../assets/img/no_active_stamp_en.png" class="img-rotate">
          </div>

        </b-row>
        <b-row v-if="pageStatus==='modify'" class="h-100 form-section">
          <b-col cols="6">
            <b-row>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.organization-number')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-input type="text" disabled
                                v-model="modifyPage.orgNumber"
                                :state="!$v.modifyPage.orgNumber.$dirty ? null : !$v.modifyPage.orgNumber.$invalid"
                                :placeholder="$t('permission-management.please-enter-organization-number')"/>
                  <b-form-invalid-feedback>
                    {{ $t('permission-management.organization-management.please-enter-organization-number') }}
                  </b-form-invalid-feedback>
                </b-form-group>
              </b-col>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.organization-name')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-input type="text"
                                v-model="modifyPage.orgName"
                                :state="!$v.modifyPage.orgName.$dirty ? null : !$v.modifyPage.orgName.$invalid"
                                :placeholder="$t('permission-management.please-enter-organization-name')"/>
                  <b-form-invalid-feedback>
                    {{ $t('permission-management.organization-management.please-enter-organization-name') }}
                  </b-form-invalid-feedback>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.parent-organization-number')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-input type="text" disabled v-model="modifyPageSelectedParentOrganizationNumber"
                                :placeholder="$t('permission-management.please-select-parent-organization')"/>
                </b-form-group>
              </b-col>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.parent-organization-name')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-select :options="parentOrganizationNameSelectOptions"
                                 :disabled="modifyPage.selectedOrg.status==='1000000701'"
                                 :state="!$v.modifyPage.parentOrgId.$dirty ? null : !$v.modifyPage.parentOrgId.$invalid"
                                 v-model="modifyPage.parentOrgId" plain/>
                  <b-form-invalid-feedback>
                    {{ $t('permission-management.organization-management.please-select-parent-organization') }}
                  </b-form-invalid-feedback>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.organization-leader')}}</template>
                  <b-form-input type="text"
                                v-model="modifyPage.leader"
                                :placeholder="$t('permission-management.please-enter-organization-leader')"/>
                </b-form-group>
              </b-col>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.organization-mobile')}}</template>
                  <b-form-input type="text"
                                v-model="modifyPage.mobile"
                                :state="!$v.modifyPage.mobile.$dirty ? null : !$v.modifyPage.mobile.$invalid"
                                :placeholder="'000-0000-0000'"/>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.organization-note')}}</template>

                  <b-form-textarea
                    v-model="modifyPage.note"
                    :placeholder="$t('permission-management.please-enter-organization-note')"
                  />

                </b-form-group>
              </b-col>
            </b-row>
          </b-col>
          <b-col cols="12" class="d-flex justify-content-end align-self-end">
            <b-button size="sm" variant="success default mr-1" @click="onModifyPageSaveButton()"><i
              class="icofont-save"/> {{
              $t('permission-management.save-button') }}
            </b-button>
            <b-button v-if="modifyPage.selectedOrg.status==='1000000702'" size="sm" variant="success default"
                      :disabled="checkPermItem('org_update_status')" @click="onAction('activate')"><i
              class="icofont-check-circled"/> {{
              $t('permission-management.active')}}
            </b-button>
            <b-button v-else-if="modifyPage.selectedOrg.status==='1000000701'" size="sm" variant="warning default mr-1"
                      :disabled="checkPermItem('org_update_status')" @click="onAction('deactivate')"><i
              class="icofont-ban"/> {{
              $t('permission-management.action-make-inactive')}}
            </b-button>
            <b-button v-if="modifyPage.selectedOrg.status==='1000000702'" size="sm" variant="danger default mr-1"
                      :disabled="checkPermItem('org_delete')" @click="onAction('delete')"><i class="icofont-bin"></i> {{
              $t('permission-management.delete')}}
            </b-button>
            <b-button size="sm" variant="primary default" @click="onModifyPageBackButton()"><i
              class="icofont-long-arrow-left"/> {{
              $t('permission-management.back-button') }}
            </b-button>
          </b-col>
          <div v-if="getLocale()==='zh'" class="position-absolute" style="left: 28%;bottom: 12%">
            <img v-if="modifyPage.selectedOrg.status==='1000000702'" src="../../../assets/img/no_active_stamp.png">
            <img v-else-if="modifyPage.selectedOrg.status==='1000000701'" src="../../../assets/img/active_stamp.png">
          </div>
          <div v-if="getLocale()==='en'" class="position-absolute" style="left: 28%;bottom: 12%">
            <img v-if="modifyPage.selectedOrg.status === '1000000702'" src="../../../assets/img/no_active_stamp_en.png"
                 class="img-rotate">
            <img v-else-if="modifyPage.selectedOrg.status === '1000000701'"
                 src="../../../assets/img/active_stamp_en.png" class="img-rotate">
          </div>
        </b-row>
        <b-row v-if="pageStatus==='show'" class="h-100 form-section">
          <b-col cols="6">
            <b-row>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.organization-number')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-input type="text" disabled
                                v-model="modifyPage.orgNumber"
                                :placeholder="$t('permission-management.please-enter-organization-number')"/>
                  <b-form-invalid-feedback>
                    {{ $t('permission-management.organization-management.please-enter-organization-number') }}
                  </b-form-invalid-feedback>
                </b-form-group>
              </b-col>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.organization-name')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-input type="text"
                                v-model="modifyPage.orgName"
                                :state="!$v.modifyPage.orgName.$dirty ? null : !$v.modifyPage.orgName.$invalid"
                                :placeholder="$t('permission-management.please-enter-organization-name')"/>
                  <b-form-invalid-feedback>
                    {{ $t('permission-management.organization-management.please-enter-organization-name') }}
                  </b-form-invalid-feedback>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.parent-organization-number')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-input type="text" disabled v-model="modifyPageSelectedParentOrganizationNumber"
                                :placeholder="$t('permission-management.please-select-parent-organization')"/>
                </b-form-group>
              </b-col>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.parent-organization-name')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-select :options="parentOrganizationNameSelectOptions"
                                 :disabled="modifyPage.selectedOrg.status==='1000000701'"
                                 :state="!$v.modifyPage.parentOrgId.$dirty ? null : !$v.modifyPage.parentOrgId.$invalid"
                                 v-model="modifyPage.parentOrgId" plain/>
                  <b-form-invalid-feedback>
                    {{ $t('permission-management.organization-management.please-select-parent-organization') }}
                  </b-form-invalid-feedback>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.organization-leader')}}</template>
                  <b-form-input type="text"
                                v-model="modifyPage.leader"
                                :placeholder="$t('permission-management.please-enter-organization-leader')"/>
                </b-form-group>
              </b-col>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.organization-mobile')}}</template>
                  <b-form-input type="text"
                                v-model="modifyPage.mobile"
                                :placeholder="'000-0000-0000'"/>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.organization-note')}}</template>

                  <b-form-textarea
                    v-model="modifyPage.note"
                    :placeholder="$t('permission-management.please-enter-organization-note')"
                  />

                </b-form-group>
              </b-col>
            </b-row>
          </b-col>
          <b-col cols="12" class="d-flex justify-content-end align-self-end">
            <b-button v-if="modifyPage.selectedOrg.status==='1000000702'" size="sm" variant="success default"
                      :disabled="checkPermItem('org_update_status')" @click="onAction('activate')"><i
              class="icofont-check-circled"/> {{
              $t('permission-management.active')}}
            </b-button>
            <b-button v-else-if="modifyPage.selectedOrg.status==='1000000701'" size="sm" variant="warning default mr-1"
                      :disabled="checkPermItem('org_update_status')" @click="onAction('deactivate')"><i
              class="icofont-ban"/> {{
              $t('permission-management.action-make-inactive')}}
            </b-button>
            <b-button size="sm" variant="primary default" @click="onModifyPageBackButton()"><i
              class="icofont-long-arrow-left"/> {{
              $t('permission-management.back-button') }}
            </b-button>
          </b-col>
          <div v-if="getLocale()==='zh'" class="position-absolute" style="left: 28%;bottom: 12%">
            <img v-if="modifyPage.selectedOrg.status==='1000000702'" src="../../../assets/img/no_active_stamp.png">
            <img v-else-if="modifyPage.selectedOrg.status==='1000000701'" src="../../../assets/img/active_stamp.png">
          </div>
          <div v-if="getLocale()==='en'" class="position-absolute" style="left: 28%;bottom: 12%">
            <img v-if="modifyPage.selectedOrg.status === '1000000702'" src="../../../assets/img/no_active_stamp_en.png"
                 class="img-rotate">
            <img v-else-if="modifyPage.selectedOrg.status === '1000000701'"
                 src="../../../assets/img/active_stamp_en.png" class="img-rotate">
          </div>
        </b-row>

      </b-tab>

      <b-tab :title="$t('permission-management.organization-structure')">

        <b-row class="h-100">
          <b-col cols="12">
            <div class="table-responsive h-100">
              <div class="bg-organization-structure text-center">
                <h3 style="font-size: 2rem;color: #1a3035" class="font-weight-bold my-4 mb-5 pb-4">
                  <span>{{$t('login.admin-title')}}</span> <span style="color: #047a98">{{$t('permission-management.organization-structure')}}</span>
                </h3>
                <vue2-org-tree
                  :data="treeData"
                  :horizontal="true"
                  :collapsable="false"
                  :label-class-name="treeLabelClass"
                  :render-content="renderTreeContent"
                />
              </div>
            </div>
          </b-col>
        </b-row>

      </b-tab>

    </b-tabs>
    <div v-show="isLoading" class="loading"></div>
    <b-modal centered ref="modal-delete" :title="$t('permission-management.prompt')">
      {{$t('permission-management.organization-delete-prompt')}}
      <template slot="modal-footer">
        <b-button variant="primary" @click="deleteOrg()" class="mr-1">
          {{$t('permission-management.modal-ok')}}
        </b-button>
        <b-button variant="danger" @click="hideModal('modal-delete')">
          {{$t('permission-management.modal-cancel')}}
        </b-button>
      </template>
    </b-modal>

    <b-modal centered ref="modal-activate" :title="$t('permission-management.prompt')">
      {{$t('permission-management.organization-activate-prompt')}}
      <template slot="modal-footer">
        <b-button variant="primary" @click="activateOrg()" class="mr-1">
          {{$t('permission-management.modal-ok')}}
        </b-button>
        <b-button variant="danger" @click="hideModal('modal-activate')">
          {{$t('permission-management.modal-cancel')}}
        </b-button>
      </template>
    </b-modal>
    <b-modal centered ref="modal-deactivate" :title="$t('permission-management.prompt')">
      {{$t('permission-management.organization-deactivate-prompt')}}
      <template slot="modal-footer">
        <b-button variant="primary" @click="deactivateOrg()" class="mr-1">
          {{$t('permission-management.modal-ok')}}
        </b-button>
        <b-button variant="danger" @click="hideModal('modal-deactivate')">
          {{$t('permission-management.modal-cancel')}}
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

  import {apiBaseUrl} from '../../../constants/config';
  import Vuetable from '../../../components/Vuetable2/Vuetable'
  import VuetablePaginationBootstrap from '../../../components/Common/VuetablePaginationBootstrap';
  import {validationMixin} from 'vuelidate';
  import Vue2OrgTree from '../../../components/vue2-org-tree'
  import {
    getApiManager,
    downLoadFileFromServer,
    printFileFromServer,
    isPhoneValid,
    getApiManagerError
  } from '../../../api';
  import {responseMessages} from '../../../constants/response-messages';
  import {checkPermissionItem, getDirection, getLocale} from '../../../utils';
  import vSelect from 'vue-select'
  import 'vue-select/dist/vue-select.css'
  import Modal from '../../../components/Modal/modal'


  const {required, maxLength} = require('vuelidate/lib/validators');

  let getOrgById = (orgData, orgId) => {
    for (let i = 0; i < orgData.length; i++) {
      if (orgData[i].orgId == orgId) {
        return orgData[i];
      }
    }
    return 0;
  };

  let fnGetOrgLevel = orgData => {
    let level = 0;
    if (orgData == null)
      return level;
    while (orgData.parent != null) {
      level++;
      orgData = orgData.parent;
    }
    return level;
  };

  export default {
    components: {
      'vuetable': Vuetable,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
      'v-select': vSelect,
      Vue2OrgTree,
      Modal
    },
    mixins: [validationMixin],
    validations: {
      fileSelection: {
        required
      },
      createPage: { // create page
        orgName: {
          required, maxLength:maxLength(50)
        },
        orgNumber: {
          required, maxLength:maxLength(50)
        },
        parentOrgId: {
          required
        },
        mobile: {
          maxLength: maxLength(25),
        }
      },
      modifyPage: { // modify page
        orgName: {
          required, maxLength:maxLength(50)
        },
        orgNumber: {
          required, maxLength:maxLength(50)
        },
        parentOrgId: {
          required
        },
        mobile: {
          maxLength: maxLength(25),
        }
      }
    },
    mounted() {
      this.handleWindowResize();
      this.$refs.vuetable.$parent.transform = this.transform.bind(this);
      this.getOrgDataAll();
    },
    data() {
      return {
        isLoading: false,
        filter: {
          orgName: '',
          status: null,
          parentOrgId: null
        }, // used for filtering table
        selectedOrg: {}, // this is used for holding data while delete and update status modals
        createPage: { // create page
          orgName: '',
          orgNumber: '',
          parentOrgId: null,
          leader: '',
          mobile: '',
          note: ''
        },
        direction: getDirection().direction,
        modifyPage: { // modify page
          selectedOrg: {},
          orgName: '',
          orgNumber: '',
          parentOrgId: null,
          leader: '',
          mobile: '',
          note: ''
        },
        renderedOrgIdList: [],
        orgData: [], // loaded from server when the page is mounted
        pageStatus: 'table', // table, create, modify -> it will change the page
        link: '',
        params: {},
        name: '',
        fileSelection: [],
        fileSelectionOptions: [
          {value: 'docx', label: 'WORD'},
          {value: 'xlsx', label: 'EXCEL'},
          {value: 'pdf', label: 'PDF'},
        ],
        isModalVisible: false,
        statusSelectOptions: [
          {value: null, text: this.$t('permission-management.all')},
          {value: '1000000701', text: this.$t('permission-management.active')},
          {value: '1000000702', text: this.$t('permission-management.inactive')}
        ],
        showLength:20,
        parentOrganizationNameSelectOptions: [], // this is used for both create and modify pages, parent org select box options
        parentOrganizationNameFilterOptions: [], // this is used for filter pages, parent org select box options
        vuetableItems: { // main table options
          apiUrl: `${apiBaseUrl}/permission-management/organization-management/organization/get-by-filter-and-page`,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: '__sequence',
              title: this.$t('permission-management.th-no'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '4%'
            },
            {
              name: '__slot:orgNumberLabel',
              title: this.$t('permission-management.th-org-number'),
              sortField: 'orgNumber',
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '7%'
            },
            {
              name: 'orgNameLabel',
              title: this.$t('permission-management.th-org-name'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '8%',
              callback: (value) => {
                if(value === null) return '';
                if(value.isLong === false) return value.groupMember;
                else{
                  return this.hoverContent(value);
                }
              },
            },
            {
              name: 'status',
              title: this.$t('permission-management.th-org-status'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '7%',
              callback: (value) => {
                const dictionary = {
                  '1000000701': `<span class="text-success">${this.$t('permission-management.org-status-active')}</span>`,
                  '1000000702': `<span class="text-muted">${this.$t('permission-management.org-status-inactive')}</span>`,
                };
                if (!dictionary.hasOwnProperty(value)) return '';
                return dictionary[value];
              }
            },
            {
              name: 'parent',
              title: this.$t('permission-management.th-org-parent-org-number'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '8%',
              callback: (value) => {

                return value ? value.orgNumber : this.$t('permission-management.org-none');

              }
            },
            {
              name: 'parent',
              title: this.$t('permission-management.th-org-parent-org-name'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '8%',
              callback: (value) => {

                return value ? value.orgName : this.$t('permission-management.org-none');

              }
            },
            {
              name: 'leader',
              title: this.$t('permission-management.th-org-leader'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '8%'
            },
            {
              name: 'mobile',
              title: this.$t('permission-management.th-org-mobile'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '15%'
            },
            {
              name: 'noteLabel',
              title: this.$t('permission-management.th-org-note'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '16%',
              callback: (value) => {
                if(value === null) return '';
                if(value.isLong === false) return value.groupMember;
                else{
                  return this.hoverContent(value);
                }
              },
            },
            {
              name: '__slot:actions',
              title: this.$t('permission-management.th-org-actions'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '16%'
            },

          ],
          perPage: 10,
        },
        treeData: { // holds tree data for org diagram
        }

      }
    },
    computed: {
      createPageSelectedParentOrganizationNumber: { // create page selected parent org number ( disabled input but automatically change)
        get() {
          let org = getOrgById(this.orgData, this.createPage.parentOrgId);
          return org ? org.orgNumber : "";
        }
      },
      modifyPageSelectedParentOrganizationNumber: { // modify page selected parent org number ( disabled input but automatically change)
        get() {
          let org = getOrgById(this.orgData, this.modifyPage.parentOrgId);
          return org ? org.orgNumber : "";
        }
      }
    },
    watch: {
      'vuetableItems.perPage': function (newVal) {
        this.$refs.vuetable.refresh();
        this.changeCheckAllStatus();
      },
      // pageStatus(newVal) {
      //   if(newVal==='table'){
      //     this.$refs.vuetable.reload();
      //   }
      // },

      orgData(newVal, oldVal) { // maybe called when the org data is loaded from server

        let id = 0;
        let nest = (items, id = 0, depth = 1) =>
          items
            .filter(item => item.parentOrgId === id)
            .map(item => ({
              ...item,
              children: nest(items, item.orgId, depth + 1),
              id: id++,
              label: `<div class="org-content-top"><span>${depth}</span>${nameLabel(item.orgNumber)}</div><div class="org-content-bottom">${item.orgName}</div>`
            }));

        let nameLabel = (orgNumber) => {

          if(orgNumber.toString().length>7) {
            orgNumber = orgNumber.substring(0, 7) + '...';
          }

          return orgNumber;
        }

        this.treeData = nest(newVal)[0];

        this.changeOrgTree(this.treeData.children, 1);
        this.parentOrganizationNameSelectOptions.unshift({
          text: this.treeData.orgName,
          value: this.treeData.orgId
        });

        this.parentOrganizationNameFilterOptions = [];
        this.parentOrganizationNameFilterOptions.push({
            value: null,
            text: this.$t('permission-management.all')
        });
        this.parentOrganizationNameSelectOptions.forEach(org => {
            this.parentOrganizationNameFilterOptions.push(org);
        })



        let getLevel = (org) => {

          let getParent = (org) => {
            for (let i = 0; i < newVal.length; i++) {
              if (newVal[i].orgId === org.parentOrgId) {
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

        let selectOptions = [];

        newVal.forEach((org) => {
          selectOptions.push({
            value: org.orgId,
            html: `${generateSpace(getLevel(org))}${org.orgName}`
          });
        });

        //this.parentOrganizationNameSelectOptions = selectOptions;
        if (selectOptions.length === 0)
          this.parentOrganizationNameSelectOptions.push({
            text: this.$t('system-setting.none'),
            value: 0
          });


      }
    },
    methods: {
      handleWindowResize(event) {
        const windowWidth = window.innerWidth;
        if(windowWidth<=1280) {
          this.showLength = 10;
        }
      },
      hoverContent(value) {
        let content = '<div class="item-wrapper slide-right">\n' +
          '      <span class="item d-flex flex-column">\n' + value.label +
          '      </span>\n' +
          '      <div class="item-extra-info flex-column d-flex">\n' + value.groupMember +
          '      </div>\n' +
          '    </div>';
        return content;
      },
      getLocale() {
        return getLocale();
      },
      selectAll(value) {
        this.$refs.vuetable.toggleAllCheckboxes('__checkbox', {target: {checked: value}});
        this.$refs.vuetable.isCheckAllStatus = value;
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.vuetable.uuid;
        let checkAllButton = document.getElementById(checkBoxId);
        checkAllButton.checked = value;
      },
      selectNone() {
        this.$refs.vuetable.isCheckAllStatus=false;
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.vuetable.uuid;
        let checkAllButton = document.getElementById(checkBoxId);
        checkAllButton.checked = false;
      },
      changeCheckAllStatus() {
        let selectList = this.$refs.vuetable.selectedTo;
        let renderedList = this.renderedOrgIdList;
        if (selectList.length >= renderedList.length) {
          let isEqual = false;
          for (let i = 0; i < renderedList.length; i++) {
            isEqual = false;
            for (let j = 0; j < selectList.length; j++) {
              if (renderedList[i] === selectList[j]) {
                j = selectList.length;
                isEqual = true
              }
            }
            if (isEqual === false) {
              this.selectNone();
              break;
            }
            if (i === renderedList.length - 1) {
              this.selectAll(true);
            }
          }
        } else {
          this.selectNone();
        }

      },
      onCheckStatusChange(isChecked) {
        if (isChecked) {
          this.changeCheckAllStatus();
        } else {
          this.selectNone();
        }
      },
      closeModal() {
        this.isModalVisible = false;
      },
      checkPermItem(value) {
        return checkPermissionItem(value);
      },

      onExportButton() {

        let checkedAll = this.$refs.vuetable.checkedAllStatus;
        let checkedIds = this.$refs.vuetable.selectedTo;
        let httpOption = this.$refs.vuetable.httpOptions;
        this.params = {
          'locale' : getLocale(),
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'sort' : httpOption.params.sort,
          'filter': this.filter,
          'idList': checkedIds.join()
        };
        this.link = `permission-management/organization-management/organization`;
        this.name = this.$t('permission-management.organization-table');
        this.isModalVisible = true;
      },

      onPrintButton() {
        let checkedAll = this.$refs.vuetable.checkedAllStatus;
        let checkedIds = this.$refs.vuetable.selectedTo;
        let httpOption = this.$refs.vuetable.httpOptions;
        let params = {
          'locale' : getLocale(),
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'sort' : httpOption.params.sort,
          'filter': this.filter,
          'idList': checkedIds.join()
        };
        let link = `permission-management/organization-management/organization`;
        printFileFromServer(link, params);
      },
      getOrgDataAll() {
        getApiManagerError().post(`${apiBaseUrl}/permission-management/organization-management/organization/get-all`, {
          type: 'with_graphic'
        }).then((response) => {
          let message = response.data.message;
          let data = response.data.data;
          switch (message) {
            case responseMessages['ok']:
              // for(let i = 0; i < data.length; i++) {
              //
              // }
              this.orgData = data;
              break;
          }
        })
      },
      generatSpace(count) {
        let string = '';
        while (count--) {
          string += '&nbsp;&nbsp;&nbsp;&nbsp;';
        }
        return string;
      },

      changeOrgTree(treeData, index) {


        if (!treeData || treeData.length === 0) {
          return;
        }

        let tmp = treeData;

        for (let i = 0; i < tmp.length; i++) {
          this.changeOrgTree(tmp[i].children, index + 1);

          this.parentOrganizationNameSelectOptions.unshift({
            value: tmp[i].orgId,
            html: `${this.generatSpace(index)}${tmp[i].orgName}`
          });
        }
      },
      onSearchButton() {
        this.$refs.vuetable.refresh();
      },
      onResetButton() {
        this.filter = {
          orgName: '',
          status: null,
          parentOrgId: null
        };
      },
      transform(response) {

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

        for (let i = 0; i < data.data.length; i++) {
          if(data.data[i].orgNumber) {
            let note = data.data[i].orgNumber.toString();

            // if (note.length > 20) {
            //   data.data[i].note = note.substr(0, 20) + "···"; // Gets the first part
            // }
            let isLong = false;
            if(note.length>this.showLength/2){
              isLong = true;
              data.data[i].orgNumberLabel = {
                groupMember : note,
                label : note.substr(0, this.showLength/2) + '...',
                isLong : isLong
              };
            }
            else {
              data.data[i].orgNumberLabel = {
                groupMember : note,
                isLong : isLong
              };
            }
          }
          if(data.data[i].orgName) {
            let note = data.data[i].orgName.toString();

            // if (note.length > 20) {
            //   data.data[i].note = note.substr(0, 20) + "···"; // Gets the first part
            // }
            let isLong = false;
            if(note.length>this.showLength/2){
              isLong = true;
              data.data[i].orgNameLabel = {
                groupMember : note,
                label : note.substr(0, this.showLength/2) + '...',
                isLong : isLong
              };
            }
            else {
              data.data[i].orgNameLabel = {
                groupMember : note,
                isLong : isLong
              };
            }
          }
          if(data.data[i].note) {
            let note = data.data[i].note.toString();

            // if (note.length > 20) {
            //   data.data[i].note = note.substr(0, 20) + "···"; // Gets the first part
            // }
            let isLong = false;
            if(note.length>this.showLength){
              isLong = true;
              data.data[i].noteLabel = {
                groupMember : note,
                label : note.substr(0, this.showLength) + '...',
                isLong : isLong
              };
            }
            else {
              data.data[i].noteLabel = {
                groupMember : note,
                isLong : isLong
              };
            }
          }
          transformed.data.push(data.data[i]);
          this.renderedOrgIdList.push(data.data[i].orgId);

        }

        return transformed

      },

      vuetableHttpFetch(apiUrl, httpOptions) { // customize data loading for table from server
        this.renderedOrgIdList = [];

        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.vuetableItems.perPage,
          sort: httpOptions.params.sort,
          filter: {
            orgName: this.filter.orgName,
            status: this.filter.status,
            parentOrgId: this.filter.parentOrgId
          }
        });
      },
      onPaginationData(paginationData) {
        this.$refs.pagination.setPaginationData(paginationData);
        this.changeCheckAllStatus();
      },
      onChangePage(page) {
        this.$refs.vuetable.changePage(page);
        this.changeCheckAllStatus();
      },
      onAction(action, data = null, index) { // called when any action button is called from table

        let modifyItem = () => {

          // rest models
          if (data.parent == null) {
            this.modifyPage = {
              selectedOrg: data,
              orgName: data.orgName,
              orgNumber: data.orgNumber,
              parentOrgId: 'None',
              leader: data.leader,
              mobile: data.mobile,
              note: data.note
            };
          } else {
            this.modifyPage = {
              selectedOrg: data,
              orgName: data.orgName,
              orgNumber: data.orgNumber,
              parentOrgId: data.parent.orgId,
              leader: data.leader,
              mobile: data.mobile,
              note: data.note
            };
          }

          // change page to modify
          this.pageStatus = 'modify';

          this.$v.modifyPage.$reset();

        };

        let showItem = () => {

          if (data.parent == null) {
            this.modifyPage = {
              selectedOrg: data,
              orgName: data.orgName,
              orgNumber: data.orgNumber,
              parentOrgId: 'None',
              leader: data.leader,
              mobile: data.mobile,
              note: data.note
            };
          } else {
            // rest models
            this.modifyPage = {
              selectedOrg: data,
              orgName: data.orgName,
              orgNumber: data.orgNumber,
              parentOrgId: data.parent.orgId,
              leader: data.leader,
              mobile: data.mobile,
              note: data.note
            };
          }

          // change page to modify
          this.pageStatus = 'show';

          this.$v.modifyPage.$reset();

        };

        let deleteItem = () => {
          this.selectedOrg = data;
          if (data == null)
            this.selectedOrg = this.modifyPage.selectedOrg;
          this.$refs['modal-delete'].show();
        };

        let activateItem = () => {

          this.selectedOrg = data;
          if (data == null)
            this.selectedOrg = this.modifyPage.selectedOrg;
          //this.deactivateOrg();
          this.$refs['modal-activate'].show();
        };

        let deactivateItem = () => {
          this.selectedOrg = data;
          if (data == null)
            this.selectedOrg = this.modifyPage.selectedOrg;
          //this.deactivateOrg();
          this.$refs['modal-deactivate'].show();
        };

        switch (action) {
          case 'show':
            showItem();
            break;
          case 'modify':
            modifyItem();
            break;
          case 'delete':
            deleteItem();
            break;
          case 'activate':
            activateItem();
            break;
          case 'deactivate':
            deactivateItem();
            break;
        }
      },
      renderTreeContent: function (h, data) { // diagram page settings

        return h('div', {
            domProps: {
              innerHTML: data.label
            }
          }
        );

        // return data.label;
      },
      treeLabelClass: function (data) {
        let level = fnGetOrgLevel(data);
        const labelClasses = ['bg-level-1', 'bg-level-2', 'bg-level-3', 'bg-level-4', 'bg-level-5'];
        return `${labelClasses[level % 5]} text-white`;
      },

      showCreatePage() { // move to create page
        // reset models
        this.createPage = {
          orgName: '',
          orgNumber: '',
          parentOrgId: null,
          leader: '',
          mobile: '',
          note: ''
        };
        // change page to create
        this.pageStatus = 'create';
        this.$v.createPage.$reset();
      },
      onCreatePageSaveButton() { // save button is clicked from create page

        this.$v.createPage.$touch();
        if (this.$v.createPage.$invalid) {
          if (this.$v.createPage.orgNumber.$invalid) {
            if(this.createPage.orgNumber === '') {
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.please-enter-organization-number`), {
                duration: 3000,
                permanent: false
              });
            }
            else {
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.organization-number-length-valid`), {
                duration: 3000,
                permanent: false
              });
            }
            return;
          }
          if (this.$v.createPage.orgName.$invalid) {
            if(this.createPage.orgName === '') {
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.please-enter-organization-name`), {
                duration: 3000,
                permanent: false
              });
            }
            else {
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.organization-name-length-valid`), {
                duration: 3000,
                permanent: false
              });
            }
            return;
          }
          if (this.$v.createPage.parentOrgId.$invalid) {
            this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.please-select-parent-organization`), {
              duration: 3000,
              permanent: false
            });
            return;
          }
          if (this.$v.createPage.mobile.$invalid) {
            this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.invalid-length-organization-mobile`), {
              duration: 3000,
              permanent: false
            });
            return;
          }
          return;
        }
        this.isLoading = true;
        // call api
        getApiManager()
          .post(`${apiBaseUrl}/permission-management/organization-management/organization/create`, {
            'orgName': this.createPage.orgName,
            'orgNumber': this.createPage.orgNumber,
            'parentOrgId': this.createPage.parentOrgId,
            'leader': this.createPage.leader,
            'mobile': this.createPage.mobile,
            'note': this.createPage.note
          })
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`permission-management.organization-created-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                // back to table
                this.pageStatus = 'table';
                this.$refs.vuetable.refresh();
                this.getOrgDataAll();
                this.isLoading = false;
                break;
              case responseMessages['used-org-name']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-org-name`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['used-org-number']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-org-number`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['has-fields']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.has-fields`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['has-user-groups']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.has-user-groups`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['has-data-groups']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.has-data-groups`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['has-roles']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.has-roles`), {
                  duration: 3000,
                  permanent: false
                });
                break;
            }
            this.isLoading = false;
          })
          .catch((error) => {
            this.isLoading = false;
          });
      },
      onCreatePageBackButton() {
        // move to table
        this.pageStatus = 'table';
      },
      onModifyPageSaveButton() { // save button is clicked from modify page

        this.$v.modifyPage.$touch();
        if (this.$v.modifyPage.$invalid) {
          if (this.$v.modifyPage.orgName.$invalid) {
            if(this.modifyPage.orgName === '') {
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.please-enter-organization-name`), {
                duration: 3000,
                permanent: false
              });
            }
            else {
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.organization-name-length-valid`), {
                duration: 3000,
                permanent: false
              });
            }
            return;
          }
          if (this.$v.modifyPage.mobile.$invalid) {
            this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.invalid-length-organization-mobile`), {
              duration: 3000,
              permanent: false
            });
            return;
          }
          return;
        }
        this.isLoading = true;
        // call api
        getApiManager()
          .post(`${apiBaseUrl}/permission-management/organization-management/organization/modify`, {
            'orgId': this.modifyPage.selectedOrg.orgId,
            'orgName': this.modifyPage.orgName,
            'orgNumber': this.modifyPage.orgNumber,
            'parentOrgId': this.modifyPage.parentOrgId,
            'leader': this.modifyPage.leader,
            'mobile': this.modifyPage.mobile,
            'note': this.modifyPage.note
          })
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']: // ok
                this.$notify('success', this.$t('permission-management.success'), this.$t(`permission-management.organization-modified-successfully`), {
                  duration: 3000,
                  permanent: false
                });

                this.pageStatus = 'table';
                this.$refs.vuetable.reload();
                this.getOrgDataAll();

                break;
              case responseMessages['has-children']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.organization-has-children`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['used-org-name']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-org-name`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['has-users']: // okay
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.organization-has-user`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['used-org-number']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-org-number`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['has-fields']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.has-fields`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['has-user-groups']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.has-user-groups`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['has-data-groups']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.has-data-groups`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['has-roles']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.has-roles`), {
                  duration: 3000,
                  permanent: false
                });
                break;
            }
            this.isLoading = false;
          })
          .catch((error) => {
            this.isLoading = false;
          });

      },
      onModifyPageBackButton() {
        // go back to main table page
        this.pageStatus = 'table';
      },
      hideModal(modal) {
        // hide modal
        this.$refs[modal].hide();
      },
      deleteOrg() {

        let org = this.selectedOrg;

        // call api
        getApiManager()
          .post(`${apiBaseUrl}/permission-management/organization-management/organization/delete`, {
            'orgId': org.orgId,
          })
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {

              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`permission-management.organization-deleted-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                this.pageStatus = 'table';
                this.$refs.vuetable.refresh();
                this.getOrgDataAll();
                break;
              case responseMessages["has-children"]: // has children
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.organization-has-children`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['has-users']: // okay
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.organization-has-user`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['has-fields']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.has-fields`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['has-user-groups']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.has-user-groups`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['has-data-groups']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.has-data-groups`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['has-roles']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.has-roles`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['active-org']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.active-org`), {
                  duration: 3000,
                  permanent: false
                });
                break;
            }
          })
          .catch((error) => {
          })
          .finally(() => {
            this.$refs['modal-delete'].hide();
          });


      },

      activateOrg() {
        let org = this.selectedOrg;
        //call api
        getApiManager()
          .post(`${apiBaseUrl}/permission-management/organization-management/organization/update-status`, {
            'orgId': org.orgId,
            'status': '1000000701',
          })
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`permission-management.organization-activated-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                if (this.modifyPage != null)
                  this.modifyPage.selectedOrg.status = '1000000701';

                this.$refs.vuetable.reload();
                this.getOrgDataAll();
                break;
              case responseMessages['has-children']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.organization-has-children`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['used-org-name']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-org-name`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['has-users']: // okay
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.organization-has-user`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['used-org-number']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-org-number`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['has-fields']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.has-fields`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['has-user-groups']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.has-user-groups`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['has-data-groups']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.has-data-groups`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['has-roles']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.has-roles`), {
                  duration: 3000,
                  permanent: false
                });
                break;

            }
          })
          .catch((error) => {
          })
          .finally(() => {
            this.$refs['modal-activate'].hide();
          });
      },

      deactivateOrg() {

        let org = this.selectedOrg;

        // call api
        getApiManager()
          .post(`${apiBaseUrl}/permission-management/organization-management/organization/update-status`, {
            'orgId': org.orgId,
            'status': '1000000702',
          })
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`permission-management.organization-deactivated-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                if (this.modifyPage != null)
                  this.modifyPage.selectedOrg.status = '1000000702';
                //this.pageStatus = 'table';
                this.$refs.vuetable.reload();
                this.getOrgDataAll();
                break;
              case responseMessages['has-children']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.organization-has-children`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['has-users']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.organization-has-user`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['has-fields']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.has-fields`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['has-user-groups']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.has-user-groups`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['has-data-groups']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.has-data-groups`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['has-roles']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.has-roles`), {
                  duration: 3000,
                  permanent: false
                });
                break;
            }
          })
          .catch((error) => {
          })
          .finally(() => {
            this.$refs['modal-deactivate'].hide();
          });

      },
    }
  }
</script>
