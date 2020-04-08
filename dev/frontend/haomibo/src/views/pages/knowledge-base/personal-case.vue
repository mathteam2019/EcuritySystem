<template>
  <div>
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb/>
        </b-colxx>
      </b-row>
    </div>
    <b-card class="main-without-tab" v-show="pageStatus === 'table'" style="margin-top: 20px;">
      <div class="h-100 d-flex flex-column">
        <b-row class="pt-2">
          <b-col cols="8">
            <b-row>
              <b-col>
                <b-form-group :label="$t('knowledge-base.task-number')">
                  <b-form-input v-model="filter.taskNumber"/>
                </b-form-group>
              </b-col>

              <b-col>
                <b-form-group :label="$t('knowledge-base.task-result')">
                  <b-form-select v-model="filter.taskResult" :options="handResultOption" plain/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group :label="$t('knowledge-base.site')">
                  <b-form-select v-model="filter.fieldId" :options="onSiteOption" plain/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group :label="$t('knowledge-base.seized-item')">
                  <b-form-select v-model="filter.handGoods" :options="onHandGoodsOption" plain/>
                </b-form-group>
              </b-col>
            </b-row>
          </b-col>
          <b-col cols="4" class="d-flex justify-content-end align-items-center">
            <div>
              <b-button size="sm" class="ml-2" variant="info default" @click="onSearchButton()">
                <i class="icofont-search-1"/>&nbsp;{{ $t('log-management.search') }}
              </b-button>
              <b-button size="sm" class="ml-2" variant="info default" @click="onResetButton()">
                <i class="icofont-ui-reply"/>&nbsp;{{$t('log-management.reset') }}
              </b-button>
              <b-button size="sm" class="ml-2" variant="outline-info default"
                        :disabled="checkPermItem('personal_knowledge_export')" @click="onExportButton()">
                <i class="icofont-share-alt"/>&nbsp;{{ $t('log-management.export')}}
              </b-button>
              <b-button size="sm" class="ml-2" variant="outline-info default"
                        :disabled="checkPermItem('personal_knowledge_print')" @click="onPrintButton()">
                <i class="icofont-printer"/>&nbsp;{{ $t('log-management.print') }}
              </b-button>
            </div>
          </b-col>
        </b-row>

        <b-row class="flex-grow-1">
          <b-col cols="12">
            <div class="table-wrapper table-responsive">
              <vuetable
                ref="pendingListTable"
                track-by="caseDealId"
                :api-url="pendingListTableItems.apiUrl"
                :fields="pendingListTableItems.fields"
                :http-fetch="pendingListTableHttpFetch"
                :per-page="pendingListTableItems.perPage"
                pagination-path="pagination"
                @vuetable:checkbox-toggled="onCheckStatusChange"
                @vuetable:pagination-data="onBlackListTablePaginationData"
                class="table-striped"
              >
                <template slot="task" slot-scope="props">
                    <span class="cursor-p text-primary"
                          @click="onRowClicked(props.rowData)">
                      {{props.rowData.task.taskNumber}}
                    </span>

                </template>
                <div slot="operating" slot-scope="props">
                  <b-button
                    size="sm"
                    variant="danger default btn-square"
                    :disabled="checkPermItem('personal_knowledge_delete')"
                    @click="showModal(props.rowData.caseDealId)">
                    <i class="icofont-bin"/>
                  </b-button>
                </div>
              </vuetable>
            </div>
            <div class="pagination-wrapper">
              <vuetable-pagination-bootstrap
                ref="pendingListTablePagination"
                :initial-per-page="pendingListTableItems.perPage"
                @vuetable-pagination:change-page="onBlackListTableChangePage"
                @onUpdatePerPage="pendingListTableItems.perPage = Number($event)"
              />
            </div>
          </b-col>
        </b-row>
      </div>
    </b-card>

    <div v-show="pageStatus === 'show'">
      <b-row class="fill-main">
        <b-col class="col-30" style="padding-left: 13px; padding-right: 8px">
          <b-card class="h-100">
            <div
              style="width: 2px; height: 13px; background-color: #0c70ab; max-width: 2px; float: left; margin-top: 5px; margin-right: 5px;"/>
            <div>
              <div style="font-size: 15px; font-weight: bold; margin-bottom: 1.5rem;">
                {{$t('personal-inspection.scanned-image')}}
              </div>
            </div>
            <b-row class="mb-1">
              <b-col style="margin-bottom: 6px;" class="icon-container">
                <div v-if="showPage.workFlow==null"></div>
                <div v-else-if="showPage.workFlow.workMode==null"></div>
                <div v-else>
                  <div v-if="showPage.workFlow.workMode.modeName===getModeDataCode('all')">
                    <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                    <b-img src="/assets/img/monitors_icon.svg" class="operation-icon"/>
                    <b-img src="/assets/img/mobile_icon.svg" class="operation-icon"/>
                  </div>
                  <div v-if="showPage.workFlow.workMode.modeName===getModeDataCode('scan')">
                    <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                  </div>
                  <div v-if="showPage.workFlow.workMode.modeName===getModeDataCode('scan+judge')">
                    <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                    <b-img src="/assets/img/monitors_icon.svg" class="operation-icon"/>
                  </div>
                  <div v-if="showPage.workFlow.workMode.modeName===getModeDataCode('scan+hand')">
                    <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                    <b-img src="/assets/img/mobile_icon.svg" class="operation-icon"/>
                  </div>
                </div>
              </b-col>
              <b-col style="margin-bottom: 5px;" class="text-right icon-container">
                <span><i class="icofont-star"/></span>
                <span v-if="judgeUserId===null || judgeUserId === defaultUserId">
                  <b-img src="/assets/img/system_scan.svg" style="width: 20px; height: 22px;"/></span>
                <span v-else><i
                    class="icofont-search-user"/></span>
                <span v-if="showPage.serScan!=null && showPage.serScan.scanImageGender==='1000000002'"><i
                  class="icofont-female"/></span>
                <span v-if="showPage.serScan!=null && showPage.serScan.scanImageGender==='1000000001'"><i
                  class="icofont-male" style="color: darkblue;"/></span>
              </b-col>
            </b-row>
            <b-row style="margin-bottom: 0.5rem;">
              <b-col style="padding-right: 0.5rem; padding-left: 1rem;">
                <canvas id="firstcanvas" style="height: 24vw;" class="img-fluid w-100 "/>
              </b-col>
              <b-col style="padding-right: 1rem; padding-left: 0.5rem;">
                <canvas id="secondcanvas"  style="height: 24vw;" class="img-fluid w-100 "/>
                <div style="width: 100%; height: 24px;" class="text-right icon-container">
                  <div v-if="power===true">
                    <b-img :disabled="power===true" src="/assets/img/previous_cartoon.png" class="operation-icon"
                           @click="previousImage()"/>
                    <b-img src="/assets/img/next_cartoon.png" class="operation-icon" @click="nextImage()"/>
                  </div>
                </div>
              </b-col>
            </b-row>
            <b-row style="float: right;">
              <b-col class="control-group">
                <div v-if="power===true" class="control-btn-wrapper">
                  <div class="control-btn">
                    <b-img src="/assets/img/contrast_btn.png" @click="onlyOneSlide(1)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.contrast')}}</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/brightness_btn.png" @click="onlyOneSlide(2)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.brightness')}}</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/color_inverse_btn.png" @click="filterId(2)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.color-inverse')}}</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/pseudo_color1_btn.png" @click="filterId(3)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.pseudo-color')}}1</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/pseudo_color2_btn.png" @click="filterId(4)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.pseudo-color')}}2</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/pseudo_color3_btn.png" @click="filterId(1)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.pseudo-color')}}3</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/pseudo_color4_btn.png" @click="filterId(12)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.pseudo-color')}}4</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/enhance_btn.png" @click="filterId(10)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.enhance')}}1</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/enhance_btn.png" @click="filterId(9)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.enhance')}}2</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/enhance_btn.png" @click="filterId(11)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.enhance')}}3</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/edge_btn.png" @click="filterId(13)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.edge')}}</span>
                  </div>
                  <div class="control-btn">
                    <b-img src="/assets/img/reduction_btn.png" @click="loadImage()"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.reduction')}}</span>
                  </div>
                </div>
                <div v-else style="opacity: 0.5" class="control-btn-wrapper">
                  <div class="control-btn">
                    <b-img src="/assets/img/contrast_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.contrast')}}</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/brightness_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.brightness')}}</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/color_inverse_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.color-inverse')}}</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/pseudo_color1_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.pseudo-color')}}1</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/pseudo_color2_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.pseudo-color')}}2</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/pseudo_color3_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.pseudo-color')}}3</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/pseudo_color4_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.pseudo-color')}}4</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/enhance_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.enhance')}}1</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/enhance_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.enhance')}}2</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/enhance_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.enhance')}}3</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/edge_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.edge')}}</span>
                  </div>
                  <div class="control-btn">
                    <b-img src="/assets/img/reduction_btn.png"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.reduction')}}</span>
                  </div>
                </div>
                <div class="switch-wrapper">
                  <div v-if="power===true" class="separator"></div>
                  <div v-else style="opacity: 0.5" class="separator"></div>
                  <div class="switch">
                    <switches v-model="power" theme="custom" :disabled="checkPermItem('personal_knowledge_toggle')"
                              color="info"/>
                  </div>
                </div>
              </b-col>
            </b-row>
            <b-row style="height: 15px !important;">
              <b-col v-if="isSlidebar2Expended" style="max-width: 100%; flex: none;">
                <vue-slider
                  v-model="slidebar2value"
                  :min="-50"
                  :max="50"
                  :dot-options="dotOptions"
                  :order="false"
                />
              </b-col>
              <b-col v-if="isSlidebar1Expended" style="max-width: 100%; flex: none;">
                <vue-slider
                  v-model="slidebar1value"
                  :min="-50"
                  :max="50"
                  :dot-options="dotOptions"
                  :order="false"
                />
              </b-col>
            </b-row>
          </b-card>
        </b-col>
        <b-col class="col-70" style="padding-right: 13px">
          <b-card class="h-100 d-flex flex-column right-card">
            <div style="height: 20px;">
              <div
                style="width: 2px; height: 13px; background-color: #0c70ab; max-width: 2px; float: left; margin-top: 5px; margin-right: 3px;"/>
              <div style="font-size: 15px; font-weight: bold;">{{$t('personal-inspection.personal')}}</div>
            </div>
            <div class="history-chart">
              <div>
                <div class="part">
                  <div class="left">
                    <div>{{$t('menu.start')}}</div>
                  </div>

                </div>

                <div class="part">
                  <div class="left">
                    <div>{{$t('maintenance-management.process-task.scan')}}</div>
                    <div>
                      <div v-if="showPage.serScan == null"></div>
                      <div v-else-if="showPage.serScan.scanPointsman == null"></div>
                      <div v-else>{{showPage.serScan.scanPointsman.userName}}</div>
                    </div>
                  </div>

                  <div class="top-date">
                    <label
                      v-if="showPage.serScan != null">{{this.getDateTimeFormat2(showPage.serScan.scanStartTime)}}</label>
                    <label v-else/>
                  </div>
                  <div class="bottom-date">
                    <label
                      v-if="showPage.serScan != null">{{this.getDateTimeFormat2(showPage.serScan.scanEndTime)}}</label>
                    <label v-else/>
                  </div>
                </div>

                <div class="part">
                  <div class="left">
                    <div>{{$t('maintenance-management.process-task.judge')}}</div>
                    <div>
                      <div v-if="judgeUserName == null">{{$t('maintenance-management.process-task.default-user')}}</div>
                      <div v-else>{{judgeUserName}}</div>
                    </div>
                  </div>

                  <div class="top-date">
                    <label v-if="judgeStartTime==null"/>
                    <label
                      v-else>{{this.getDateTimeFormat2(judgeStartTime)}}</label>
                  </div>
                  <div class="bottom-date">
                    <label v-if="showPage.serJudgeGraph==null"/>
                    <label
                      v-else>{{this.getDateTimeFormat2(showPage.serJudgeGraph.judgeEndTime)}}</label>
                    <label v-else/>
                  </div>
                </div>

                <div class="part">
                  <div class="left">
                    <div>{{$t('device-config.maintenance-config.inspection')}}</div>
                    <div>
                      <div v-if="handUserName == null"></div>
                      <div v-else>{{handUserName}}</div>
                    </div>
                  </div>

                  <div class="top-date">
                    <label v-if="handStartTime == null || showPage.workFlow.workMode.modeName===getModeDataCode('scan+judge') || showPage.workFlow.workMode.modeName===getModeDataCode('scan')"/>
                    <label
                      v-else>{{this.getDateTimeFormat2(handStartTime)}}</label>
                  </div>
                  <div class="bottom-date">
                    <label v-if="showPage.serHandExamination == null || showPage.workFlow.workMode.modeName===getModeDataCode('scan+judge') || showPage.workFlow.workMode.modeName===getModeDataCode('scan')"/>
                    <label
                      v-else>{{this.getDateTimeFormat2(showPage.serHandExamination.handEndTime)}}</label>
                  </div>
                </div>

                <div class="part">
                  <div class="left">
                    <div>{{$t('menu.end')}}</div>
                  </div>

                </div>

              </div>
            </div>
            <b-row>
              <b-col>
                <b-form-group class="form-group-margin">
                  <template slot="label">
                    {{$t('personal-inspection.task-number')}}
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input disabled v-model="showPage.taskNumber"
                                class="form-input-border"/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group class="form-group-margin">
                  <template slot="label">
                    {{$t('personal-inspection.on-site')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input disabled v-if="showPage.field==null" class="form-input-border"/>
                  <b-form-input disabled v-else v-model="showPage.field.fieldDesignation"
                                class="form-input-border"/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group class="form-group-margin">
                  <template slot="label">
                    {{$t('personal-inspection.security-instrument')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input disabled v-if="showPage.serScan == null"
                                class="form-input-border"/>
                  <b-form-input disabled v-else-if="showPage.serScan.scanDevice == null"
                                class="form-input-border"/>
                  <b-form-input disabled v-else v-model="showPage.serScan.scanDevice.deviceName"
                                class="form-input-border"/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group class="form-group-margin">
                  <template slot="label">
                    {{$t('personal-inspection.image-gender')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input disabled class="form-input-border"
                                v-if="showPage.serScan == null"/>
                  <b-form-input disabled class="form-input-border" v-else
                                :value="getOptionValue(showPage.serScan.scanImageGender)"/>
                </b-form-group>
              </b-col>
            </b-row>

            <b-row>
              <b-col>
                <b-form-group class="form-group-margin">
                  <template slot="label">
                    {{$t('personal-inspection.hand-check-station')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input disabled class="form-input-border"
                                v-if="handDeviceName == null"/>
                  <b-form-input disabled class="form-input-border" v-else
                                v-model="handDeviceName"/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group class="form-group-margin">
                  <template slot="label">
                    {{$t('personal-inspection.judgement-station')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input disabled class="form-input-border"
                                v-if="judgeDeviceName == null"/>
                  <b-form-input disabled class="form-input-border" v-else
                                v-model="judgeDeviceName"/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group class="form-group-margin">
                  <template slot="label">
                    {{$t('personal-inspection.operation-mode')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input disabled class="form-input-border"
                                v-if="showPage.workFlow==null"/>
                  <b-form-input disabled class="form-input-border"
                                v-else-if="showPage.workFlow.workMode==null"/>
                  <b-form-input disabled class="form-input-border" v-else
                                :value="getModeName(showPage.workFlow.workMode.modeName)"/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group class="form-group-margin">
                  <template slot="label">
                    {{$t('personal-inspection.judgement-conclusion-type')}}
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input disabled class="form-input-border"
                                v-if="judgeUserId===null || judgeUserId === defaultUserId"
                                :value="$t('maintenance-management.process-task.system')"/>
                  <b-form-input disabled class="form-input-border" v-else
                                :value="$t('maintenance-management.process-task.artificial')"/>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col>
                <b-form-group class="form-group-margin">
                  <template slot="label">
                    {{$t('personal-inspection.evaluation-chart')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input disabled class="form-input-border"
                                v-if="showPage.history == null"/>
                  <b-form-input disabled class="form-input-border" v-else
                                :value="getOptionValue(showPage.history.handAppraise) + ' ' + getOptionValue(showPage.history.handAppraiseSecond)"/>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group class="form-group-margin">
                  <template slot="label">
                    {{$t('personal-inspection.history-offline')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input disabled class="form-input-border"
                                v-if="showPage.serScan == null || showPage.serScan.scanOffLine==null" :value="getOptionValue(0)"/>
                  <b-form-input disabled class="form-input-border" v-else
                                :value="getOptionValue(showPage.serScan.scanOffLine)"/>
                </b-form-group>
              </b-col>
              <b-col>
              </b-col>
              <b-col>
              </b-col>
            </b-row>
            <b-row>
              <b-col>
                <b-form-group class="form-group-margin">
                  <template slot="label">
                     {{$t('permission-management.note')}}
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input disabled class="form-input-border" style="max-width: 100%;"
                                v-if="showPage.note == null"/>
                  <b-form-input disabled class="form-input-border" style="max-width: 100%;" v-else
                                v-model="showPage.note"/>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col>
                <div
                  style="width: 2px; height: 13px; background-color: #0c70ab; max-width: 2px; float: left; margin-top: 5px; margin-right: 3px;"/>
                <label
                  style="font-size: 15px; font-weight: bold;">{{$t('personal-inspection.seized-contraband')}}</label>
                <b-row class="justify-content-start" style="margin-bottom: 2rem; margin-top: 1rem">
                  <b-col>
                    <div v-if="handGoodExpanded[0]" class="text-center"
                         style="background-color: #ff0000; padding-top: 8px; padding-bottom: 8px; border-radius: 17px">
                      <span>{{handGoodDataCodeValue[handGoodDataCodeExpanded[0]].text}}</span>
                    </div>
                  </b-col>
                  <b-col>
                    <div v-if="handGoodExpanded[1]" class="text-center"
                         style="background-color: #ff4e00; padding-top: 8px; padding-bottom: 8px; border-radius: 17px">
                      <span>{{handGoodDataCodeValue[handGoodDataCodeExpanded[1]].text}}</span>
                    </div>
                  </b-col>
                  <b-col>
                    <div v-if="handGoodExpanded[2]" class="text-center"
                         style="background-color: #ff7e00; padding-top: 8px; padding-bottom: 8px; border-radius: 17px">
                      <span>{{handGoodDataCodeValue[handGoodDataCodeExpanded[2]].text}}</span>
                    </div>
                  </b-col>
                  <b-col>
                    <div v-if="handGoodExpanded[3]" class="text-center"
                         style="background-color: #ffae00; padding-top: 8px; padding-bottom: 8px; border-radius: 17px">
                      <span>{{handGoodDataCodeValue[handGoodDataCodeExpanded[3]].text}}</span>
                    </div>
                  </b-col>
                  <b-col>
                    <div v-if="handGoodExpanded[4]" class="text-center"
                         style="background-color: #ffae00; padding-top: 8px; padding-bottom: 8px; border-radius: 17px">
                      <span>{{handGoodDataCodeValue[handGoodDataCodeExpanded[4]].text}}</span>
                    </div>
                  </b-col>
                </b-row>

                <div
                  style="width: 2px; height: 13px; background-color: #0c70ab; max-width: 2px; float: left; margin-top: 5px; margin-right: 3px;"/>
                <label
                  style="font-size: 15px; font-weight: bold;">{{$t('personal-inspection.obtained-evidence')}}</label>
                <b-row class="evidence-gallery" style="margin-top: 0.5rem">
                  <b-col cols="auto" v-for="(thumb, thumbIndex) in thumbs" :key="`thumb_${thumbIndex}`"
                         @click="onThumbClick(thumbIndex)">
                    <img :src="thumb.src" style="width: 60px; height: 45px;" :alt="thumb.name"/>

                  </b-col>
                  <b-col cols="auto" v-for="(video, videoIndex) in videos" :key="`video_${videoIndex}`"
                         @click="onVideoClick(video)">
                    <video style=" width: 60px; height: 50px;">
                      <source :src="video.src" type="video/mp4">
                    </video>
                  </b-col>
                  <light-gallery :images="images" :index="photoIndex" :disable-scroll="true" @close="handleHide()"/>
                </b-row>
              </b-col>
              <b-col style="max-width: 45%;">
                <b-row>
                  <b-col cols="12" class="align-self-end text-right mt-3" style="height: 130px;">
                    <b-img v-if="validIcon === 'TRUE'" hidden src="/assets/img/icon_invalid.png" class="align-self-end"
                           style="width: 100px; height: 95px;"/>
                    <b-img v-else hidden src="/assets/img/icon_valid.png" class="align-self-end"
                           style="width: 100px; height: 95px;"/>

                  </b-col>
                </b-row>
                <b-row style="margin-top: 1rem">
                  <b-col cols="12" class="align-self-end text-right mt-3">

                    <b-button size="sm" variant="danger default"
                              :disabled="checkPermItem('personal_knowledge_delete')"
                              @click="showModal(caseDealId)">
                      <i class="icofont-bin"/> {{$t('system-setting.delete')}}
                    </b-button>
                    <b-button size="sm" variant="info default" @click="pageStatus='table'">
                      <i class="icofont-long-arrow-left"/>
                      {{ $t('personal-inspection.return') }}
                    </b-button>
                  </b-col>
                </b-row>

              </b-col>
            </b-row>
          </b-card>
        </b-col>
      </b-row>
      <div class="video-wrapper" v-show="showVideo">
        <div class="video-container">
          <video-player ref="videoPlayer" :options="videoOptions"/>
        </div>
        <span class="switch-action" @click="finishVideoShow()"><i class="icofont-close-line"/></span>
      </div>
    </div>
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
    <b-modal centered id="modal-dismiss" ref="modal-dismiss" :title="$t('system-setting.prompt')">
      {{$t('site-management.delete-prompt')}}
      <template slot="modal-footer">
        <b-button variant="primary" @click="onAction(caseDealId)" class="mr-1">
          {{$t('system-setting.ok')}}
        </b-button>
        <b-button variant="danger" @click="hideModal('modal-dismiss')">{{$t('system-setting.cancel')}}
        </b-button>
      </template>
    </b-modal>
    <Modal
      ref="exportModal"
      v-if="isModalVisible"
      :link="link" :params="params" :name="name" :imgLink="imgUrl"
      @close="closeModal"
    />

  </div>
</template>
<style lang="scss">
  .col-form-label {
    margin-bottom: 1px;
  }
  .col-30{
    -webkit-box-flex: 0;
    -ms-flex: 0 0 30%;
    flex: 0 0 30%;
    max-width: 30%;
  }
  .col-70{
    -webkit-box-flex: 0;
    -ms-flex: 0 0 70%;
    flex: 0 0 70%;
    max-width: 70%;
  }
  .form-group-margin{
    margin-bottom: 1.5rem;
  }
  .form-input-border{
    background-color: white !important;
    border: 1px solid #ebebeb;
  }
  span.cursor-p {
    cursor: pointer !important;
  }

  .slide-class {
    margin-top: -30px;
  }

  .rounded-span {
    width: 20px;
    height: 20px;
    border-radius: 10px;
    cursor: pointer;
    background-color: #007bff;
  }

  .operation-icon {
    width: 24px;
    height: 24px;
    margin-right: 8px;
  }

  .icon-container {
    font-size: 20px;

    .icofont-star {
      color: #ffe400;
    }

    .icofont-search-user {
      color: #ff9c0e;
    }

    .icofont-female {
      color: #fe687f;
    }
  }

  .control-group {
    display: flex;
    align-items: flex-start;

    .control-btn-wrapper {
      display: flex;
      flex-grow: 1;
      flex-wrap: wrap;

      .control-btn {
        width: calc(100% / 6);
        display: flex;
        flex-direction: column;
        align-items: center;
        margin-bottom: 5px;

        img {
          $size: 35px;
          width: $size;
          height: $size;
          margin-bottom: 6px;
        }

        span {
          display: block;
        }
      }
    }

    .switch-wrapper {
      width: 60px;
      height: 40px;
      display: flex;
      align-items: center;

      .separator {
        border: 0;
        width: 1px;
        height: 30px;
        background: #1e9dd2;
        flex-shrink: 0;
      }

      .switch {
        .vue-switcher {
          display: flex;
          height: 100%;
          margin: 0;
          transform: scale(0.8);
        }
      }
    }

    @media screen and (max-width: 1700px) {

      .control-btn-wrapper {
        .control-btn {
          img {
            $size: 28px;
            width: $size !important;
            height: $size !important;
          }
        }
      }
      .switch-wrapper {
        height: 28px;

        .separator {
          height: 28px;
        }
      }

    }
  }

  .history-chart {

    $ratio: 12.8;

    width: 100%;
    padding-bottom: 100% / $ratio;
    position: relative;

    margin-bottom: 24px;

    & > :first-child {
      left: 0;
      height: 100%;
      position: absolute;
      top: 0;
      width: 100%;

      background: url("/assets/img/history_chart.png") no-repeat;
      background-size: contain;

      $elements: 5;
      @for $i from 0 to $elements {
        .part:nth-child(#{$i + 1}) {
          position: absolute;
          top: 25%;
          bottom: 25%;
          left: 2% + 20% * $i;
          width: 20% - 4%;
          display: flex;
          color: white;
          align-items: center;
          justify-content: space-between;

          $date-color: #0c70ab;

          .top-date {
            color: $date-color;
            position: absolute;
            top: 104%;
            left: -6%;
          }

          .bottom-date {
            color: $date-color;
            position: absolute;
            bottom: 104%;
            right: 2%;
          }
        }
      }
    }
  }

  .evidence-gallery {
    .col-auto {
      padding-right: 0px;
    }
  }

</style>
<script>

  import {apiBaseUrl} from "../../../constants/config";
  import Vuetable from '../../../components/Vuetable2/Vuetable'
  import VuetablePaginationBootstrap from '../../../components/Common/VuetablePaginationBootstrap'
  import vSelect from 'vue-select';
  import 'vue-select/dist/vue-select.css'
  import {
    getApiManager,
    getDateTimeWithFormat,
    downLoadFileFromServer,
    printFileFromServer,
    getApiManagerError
  } from '../../../api';
  import {responseMessages} from '../../../constants/response-messages';
  import 'vue-tree-halower/dist/halower-tree.min.css'
  import Switches from 'vue-switches';
  import {LightGallery} from 'vue-light-gallery';
  import DatePicker from 'vue2-datepicker';
  import 'vue2-datepicker/index.css';
  import 'vue2-datepicker/locale/zh-cn';
  import {loadImageCanvas, imageFilterById, getDirection, getLocale} from '../../../utils'
  import VueSlideBar from 'vue-slide-bar'
  import {checkPermissionItem} from "../../../utils";
  import Videoplayer from '../../../components/Common/VideoPlayer';
  import {validationMixin} from "vuelidate";
  import Modal from '../../../components/Modal/modal';
  import VueSlider from 'vue-slider-component'
  import 'vue-slider-component/theme/default.css'

  const {required, email, minLength, maxLength, alphaNum} = require('vuelidate/lib/validators');

  export default {
    components: {
      'v-select': vSelect,
      'vuetable': Vuetable,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
      'switches': Switches,
      'light-gallery': LightGallery,
      'date-picker': DatePicker,
      'video-player': Videoplayer,
      VueSlideBar,
      VueSlider,
      Modal
    },
    mixins: [validationMixin],
    validations: {
      fileSelection: {
        required
      },
    },
    mounted() {
      this.getSiteOption();
    },
    data() {
      return {
      value1: 0,
        value2: [0, 0],
        dotOptions: [{
          disabled: true
        }, {
          disabled: false
        }],
        videoOptions: {
          autoplay: true,
          language: 'zh',//todo need to set that lang setting with multiple.
          poster: '/assets/img/glock-thumb.jpg', //todo need to set its image data differently if needed
          sources: [{
            type: "video/mp4",
            src: '/assets/img/113.mp4',
          }],
        },
        selectedVideo: null,
        isExpanded: false,
        pageStatus: 'table',
        power: true,
        siteData: [],
        showPage: [],
        renderedCheckList:[],
        link: '',
        params: {},
        name: '',
        fileSelection : [],
        direction: getDirection().direction,
        fileSelectionOptions: [
          {value: 'docx', label: 'WORD'},
          {value: 'xlsx', label: 'EXCEL'},
          {value: 'pdf', label: 'PDF'},
        ],
        isModalVisible: false,

        isSlidebar1Expended: false,
        isSlidebar2Expended: false,
        slidebar1value: [0, 0],
        slidebar2value: [0, 0],
        // isSlidebar3Expended:false,
        // isSlidebar4Expended:false,
        // slidebar3value:0,
        // slidebar4value:0,
        caseDealId: 0,
        slider: {
          lineHeight: 10,
          processStyle: {
            backgroundColor: 'blue'
          }
        },
        isCheckAll: false,
        validIcon: null,
        apiBaseURL: '',
        idList: [],
        filter: {
          fieldId: null,
          caseStatus: "1000002503",
          taskNumber: null,
          taskResult: null,
          fieldDesignation: null,
          handGoods: null,
        },

        timeData: [],
        imageUrls: [],
        handGoodDataCode: ['1000001601', '1000001602', '1000001603', '1000001604', '1000001605'],
        handGoodExpanded: [false, false, false, false, false],
        handGoodDataCodeExpanded: [],
        handGoodDataCodeValue: {
          1000001601: {text: '安眠药'},
          1000001602: {text: '仿真枪'},
          1000001603: {text: '玩具枪'},
          1000001604: {text: '气枪'},
          1000001605: {text: '打火机'},
        },

        operationModeOptions: [
          {value: null, text: this.$t('personal-inspection.all')},
          {value: '1000001301', text: this.$t('personal-inspection.security-instrument')},
          {value: '1000001302', text: this.$t('personal-inspection.security-instrument-and-hand-test')},
          {value: '1000001303', text: this.$t('personal-inspection.security-instrument-and-manual-test')},
          {value: '1000001304', text: this.$t('personal-inspection.security-instrument-and-hand-test-and-device')},
        ],

        handResultOption: [
          {value: null, text: this.$t('personal-inspection.all')},
          {value: 'TRUE', text: this.$t('knowledge-base.seized')},
          {value: 'FALSE', text: this.$t('knowledge-base.no-seized')},
        ],

        onSiteOption: [],
        onHandGoodsOption: [
          {value: null, text: this.$t('personal-inspection.all')},
          {value: '1000001601', text: '安眠药'},
          {value: '1000001602', text: '仿真枪'},
          {value: '1000001603', text: '玩具枪'},
          {value: '1000001604', text: '气枪'},
          {value: '1000001605', text: '打火机'}
        ],
        pendingListTableItems: {
          apiUrl: `${apiBaseUrl}/knowledge-base/get-by-filter-and-page`,
          perPage: 10,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__sequence',
              title: this.$t('knowledge-base.th-no'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:task',
              title: this.$t('knowledge-base.task-number'),
              sortField: 'taskNumber',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'handTaskResult',
              title: this.$t('knowledge-base.task-result'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (handTaskResult) => {

                const dictionary = {
                  "TRUE": `<span style="color:#ef6e69;">${this.$t('knowledge-base.seized')}</span>`,
                  "FALSE": `<span style="color:#e8a23e;">${this.$t('knowledge-base.no-seized')}</span>`,
                };

                if (handTaskResult == null) return '';
                if (!dictionary.hasOwnProperty(handTaskResult)) return '';
                return dictionary[handTaskResult];
              }
            },
            {
              name: 'task',
              title: this.$t('knowledge-base.site'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (task) => {
                if (task == null) return '';
                if (task.field == null) return '';
                return task.field.fieldDesignation;
              }
            },
            {
              name: 'scanDevice',
              title: this.$t('knowledge-base.channel'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (scanDevice) => {
                if (scanDevice == null) return '';
                return scanDevice.devicePassageWay;
              }
            },

            {
              name: 'handGoods',
              title: this.$t('knowledge-base.seized-item'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (handGoods) => {
                if (handGoods == null) return '';
                return this.getHandGoodString(handGoods);
              }
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

        thumbs: [],
        images: [],
        videos: [],
        photoIndex: null,
        showVideo: false,

        widthRate: [],
        heightRate: [],
        imgRect: [],
        cartoonRect: [],
        rRects: [],
        rectAdd: [],
        rectDel: [],

        imagesInfo: [],
        cartoonsInfo: [],
        imageRectR: [],
        imageRectL: [],
        cartoonRectR: [],
        cartoonRectL: [],
        cntCartoon: 0,
        orderCartoon: 0,
        mode:null,
        conclusionType:null,
        defaultUserId: 10000,
        modal_video_url: "",
        detailForm: {},
        judgeStartTime: null,
        judgeDeviceName: null,
        judgeUserName: null,
        judgeUserId: null,
        handStartTime: null,
        handDeviceName: null,
        handUserName: null,
      }
    },
    watch: {
      'pendingListTableItems.perPage': function (newVal) {
        this.$refs.pendingListTable.refresh();
        this.changeCheckAllStatus();
      },
      siteData: function (newVal, oldVal) {
        this.onSiteOption = [];
        this.onSiteOption = newVal.map(site => ({
          text: site.fieldDesignation,
          value: site.fieldId
        }));
        this.onSiteOption.push({
          text: this.$t('personal-inspection.all'),
          value: null
        });
        if (this.onSiteOption.length === 0)
          this.onSiteOption.push({
            text: this.$t('system-setting.none'),
            value: 0
          });
      },

      power(newValue) {
        this.isSlidebar1Expended = false;
        this.isSlidebar2Expended = false;
        this.loadImage();
      },

      slidebar1value(newsValue, oldValue) {

        if(oldValue[1]<newsValue[1]) {
          for(let i=oldValue[1]; i<newsValue[1]; i++) {
            this.filterId(5);
          }
        }
        else {
          for(let i=newsValue[1]; i<oldValue[1]; i++) {
            this.filterId(6);
          }
        }
      },

      slidebar2value(newsValue, oldValue) {

        if(oldValue[1]<newsValue[1]) {
          for(let i=oldValue[1]; i<newsValue[1]; i++) {
            this.filterId(7);
          }
        }
        else {
          for(let i=newsValue[1]; i<oldValue[1]; i++) {
            this.filterId(8);
          }
        }
      },
    },
    methods: {
      selectAll(value){
        this.$refs.pendingListTable.toggleAllCheckboxes('__checkbox', {target: {checked: value}});
        this.$refs.pendingListTable.isCheckAllStatus=value;
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.pendingListTable.uuid;
        let checkAllButton =  document.getElementById(checkBoxId);
        checkAllButton.checked = value;
      },
      selectNone(){
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.pendingListTable.uuid;
        let checkAllButton =  document.getElementById(checkBoxId);
        checkAllButton.checked = false;
      },
      changeCheckAllStatus(){
        let selectList = this.$refs.pendingListTable.selectedTo;
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
      onCheckStatusChange(isChecked){
        if(isChecked){
          this.changeCheckAllStatus();
        }
        else {
          this.selectNone();
        }
      },
      closeModal() {
        this.isModalVisible = false;
      },
      checkPermItem(value) {
        return checkPermissionItem(value);
      },

      onVideoClick(video) {
        this.videoOptions.sources.src = video.src;
        this.$refs.videoPlayer.initialize();
        this.showVideo = true;
      },

      finishVideoShow() {
        this.showVideo = false;
        this.$refs.videoPlayer.dispose();
      },

      onThumbClick(index) {

        this.photoIndex = index;
        this.isOpen = true;
      },

      handleHide() {
        this.photoIndex = null;
        this.isOpen = false;
      },

      onlyOneSlide(value) {
        if (this.power === true) {
          if (value === 1) {
            this.isSlidebar1Expended = !this.isSlidebar1Expended;
            this.isSlidebar2Expended = !this.isSlidebar1Expended;
          }
          if (value === 2) {
            this.isSlidebar2Expended = !this.isSlidebar2Expended;
            this.isSlidebar1Expended = !this.isSlidebar2Expended;
          }
        }
      },

      filterId(id) {
        if (id < 5 || id > 8) {
          this.isSlidebar1Expended = false;
          this.isSlidebar2Expended = false;

        }
        if (this.power === true) {
          imageFilterById(id, this.cartoonRectL, this.cartoonRectR);
        }
      },

      loadImage() {
        let url1 = '';
        let url2 = '';
        this.slidebar1value = [0, 0];
        this.slidebar2value = [0, 0];
        if (this.power === false) {

          if (this.imagesInfo[0] !== undefined) {
            url1 = this.imagesInfo[0].imageUrl;
          }
          if (this.imagesInfo[1] !== undefined) {
            url2 = this.imagesInfo[1].imageUrl;
          }
          loadImageCanvas(url1, url2, this.imageRectL, this.imageRectR, this.power);

        } else {
          let k = this.orderCartoon * 2;
          this.cartoonRectL = [];
          this.cartoonRectR = [];

          if (this.cartoonsInfo[k] !== undefined) {
            url1 = this.cartoonsInfo[k].imageUrl;
            if(this.cartoonsInfo[k].imageRect!=null) {
              if(this.cartoonsInfo[k].rectsDel != null){
                for (let i = 0; i < this.cartoonsInfo[k].imageRect.length; i++) {
                  let isDeleted = false;
                  for (let j = 0; j < this.cartoonsInfo[k].rectsDel.length; j++) {
                    if(this.cartoonsInfo[k].imageRect[i].x === this.cartoonsInfo[k].rectsDel[j].x && this.cartoonsInfo[k].imageRect[i].y === this.cartoonsInfo[k].rectsDel[j].y && this.cartoonsInfo[k].imageRect[i].width === this.cartoonsInfo[k].rectsDel[j].width && this.cartoonsInfo[k].imageRect[i].height === this.cartoonsInfo[k].rectsDel[j].height) {
                      isDeleted = true;
                      break;
                    }
                  }
                  if(!isDeleted) {
                    this.cartoonRectL.push({
                      x: this.cartoonsInfo[k].rateWidth * this.cartoonsInfo[k].imageRect[i].x,
                      y: this.cartoonsInfo[k].rateHeight * this.cartoonsInfo[k].imageRect[i].y,
                      width: this.cartoonsInfo[k].rateWidth * this.cartoonsInfo[k].imageRect[i].width,
                      height: this.cartoonsInfo[k].rateHeight * this.cartoonsInfo[k].imageRect[i].height,
                      colour: this.cartoonsInfo[k].colorRect,
                    });
                  }
                }
                for (let i = 0; i < this.cartoonsInfo[k].rectsDel.length; i++) {
                  let isDeleted = false;
                  for (let j = 0; j < this.cartoonsInfo[k].imageRect.length; j++) {
                    if(this.cartoonsInfo[k].rectsDel[i].x === this.cartoonsInfo[k].imageRect[j].x && this.cartoonsInfo[k].rectsDel[i].y === this.cartoonsInfo[k].imageRect[j].y && this.cartoonsInfo[k].rectsDel[i].width === this.cartoonsInfo[k].imageRect[j].width && this.cartoonsInfo[k].rectsDel[i].height === this.cartoonsInfo[k].imageRect[j].height) {
                      isDeleted = true;
                      break;
                    }
                  }
                  if(!isDeleted) {
                    this.cartoonsInfo[k].rectsDel[i].x = 0;
                    this.cartoonsInfo[k].rectsDel[i].y = 0;
                    this.cartoonsInfo[k].rectsDel[i].width = 0;
                    this.cartoonsInfo[k].rectsDel[i].height = 0;
                  }
                }
              }
              else {
                for (let i = 0; i < this.cartoonsInfo[k].imageRect.length; i++) {
                  this.cartoonRectL.push({
                    x: this.cartoonsInfo[k].rateWidth * this.cartoonsInfo[k].imageRect[i].x,
                    y: this.cartoonsInfo[k].rateHeight * this.cartoonsInfo[k].imageRect[i].y,
                    width: this.cartoonsInfo[k].rateWidth * this.cartoonsInfo[k].imageRect[i].width,
                    height: this.cartoonsInfo[k].rateHeight * this.cartoonsInfo[k].imageRect[i].height,
                    colour: this.cartoonsInfo[k].colorRect,
                  });
                }
              }
            }
            if (this.cartoonsInfo[k].rectsAdd != null) {
            for (let i = 0; i < this.cartoonsInfo[k].rectsAdd.length; i++) {

                this.cartoonRectL.push({
                  x: this.cartoonsInfo[k].rateWidth * this.cartoonsInfo[k].rectsAdd[i].x,
                  y: this.cartoonsInfo[k].rateHeight * this.cartoonsInfo[k].rectsAdd[i].y,
                  width: this.cartoonsInfo[k].rateWidth * this.cartoonsInfo[k].rectsAdd[i].width,
                  height: this.cartoonsInfo[k].rateHeight * this.cartoonsInfo[k].rectsAdd[i].height,
                  colour: this.cartoonsInfo[k].colorAdd,
                });
              }
            }

            if (this.cartoonsInfo[k].displayDel === '1000000601') {
              if (this.cartoonsInfo[k].rectsDel != null) {
                for (let i = 0; i < this.cartoonsInfo[k].rectsDel.length; i++) {
                  this.cartoonRectL.push({
                    x: this.cartoonsInfo[k].rateWidth * this.cartoonsInfo[k].rectsDel[i].x,
                    y: this.cartoonsInfo[k].rateHeight * this.cartoonsInfo[k].rectsDel[i].y,
                    width: this.cartoonsInfo[k].rateWidth * this.cartoonsInfo[k].rectsDel[i].width,
                    height: this.cartoonsInfo[k].rateHeight * this.cartoonsInfo[k].rectsDel[i].height,
                    colour: this.cartoonsInfo[k].colorDel,
                  });
                }
              }
            }
          }

          if (this.cartoonsInfo[k + 1] !== undefined) {
            url2 = this.cartoonsInfo[k + 1].imageUrl;
            if (this.cartoonsInfo[k + 1].imageRect != null) {
              if(this.cartoonsInfo[k + 1].rectsDel != null){
                for (let i = 0; i < this.cartoonsInfo[k+ 1].imageRect.length; i++) {
                  let isDeleted = false;
                  for (let j = 0; j < this.cartoonsInfo[k+ 1].rectsDel.length; j++) {
                    if(this.cartoonsInfo[k+ 1].imageRect[i].x === this.cartoonsInfo[k+ 1].rectsDel[j].x && this.cartoonsInfo[k+ 1].imageRect[i].y === this.cartoonsInfo[k+ 1].rectsDel[j].y && this.cartoonsInfo[k+ 1].imageRect[i].width === this.cartoonsInfo[k+ 1].rectsDel[j].width && this.cartoonsInfo[k+ 1].imageRect[i].height === this.cartoonsInfo[k+ 1].rectsDel[j].height) {
                      isDeleted = true;
                      break;
                    }
                  }
                  if(!isDeleted) {
                    this.cartoonRectR.push({
                      x: this.cartoonsInfo[k + 1].rateWidth * this.cartoonsInfo[k + 1].imageRect[i].x,
                      y: this.cartoonsInfo[k + 1].rateHeight * this.cartoonsInfo[k + 1].imageRect[i].y,
                      width: this.cartoonsInfo[k + 1].rateWidth * this.cartoonsInfo[k + 1].imageRect[i].width,
                      height: this.cartoonsInfo[k + 1].rateHeight * this.cartoonsInfo[k + 1].imageRect[i].height,
                      colour: this.cartoonsInfo[k + 1].colorRect,
                    });
                  }
                }
                for (let i = 0; i < this.cartoonsInfo[k + 1].rectsDel.length; i++) {
                  let isDeleted = false;
                  for (let j = 0; j < this.cartoonsInfo[k + 1].imageRect.length; j++) {
                    if(this.cartoonsInfo[k + 1].rectsDel[i].x === this.cartoonsInfo[k + 1].imageRect[j].x && this.cartoonsInfo[k + 1].rectsDel[i].y === this.cartoonsInfo[k + 1].imageRect[j].y && this.cartoonsInfo[k + 1].rectsDel[i].width === this.cartoonsInfo[k + 1].imageRect[j].width && this.cartoonsInfo[k + 1].rectsDel[i].height === this.cartoonsInfo[k + 1].imageRect[j].height) {
                      isDeleted = true;
                      break;
                    }
                  }
                  if(!isDeleted) {
                    this.cartoonsInfo[k + 1].rectsDel[i].x = 0;
                    this.cartoonsInfo[k + 1].rectsDel[i].y = 0;
                    this.cartoonsInfo[k + 1].rectsDel[i].width = 0;
                    this.cartoonsInfo[k + 1].rectsDel[i].height = 0;
                  }
                }
              }
              else {
                for (let i = 0; i < this.cartoonsInfo[k + 1].imageRect.length; i++) {
                  this.cartoonRectR.push({
                    x: this.cartoonsInfo[k + 1].rateWidth * this.cartoonsInfo[k + 1].imageRect[i].x,
                    y: this.cartoonsInfo[k + 1].rateHeight * this.cartoonsInfo[k + 1].imageRect[i].y,
                    width: this.cartoonsInfo[k + 1].rateWidth * this.cartoonsInfo[k + 1].imageRect[i].width,
                    height: this.cartoonsInfo[k + 1].rateHeight * this.cartoonsInfo[k + 1].imageRect[i].height,
                    colour: this.cartoonsInfo[k + 1].colorRect,
                  });
                }
              }
            }

            if (this.cartoonsInfo[k + 1].rectsAdd != null) {
              for (let i = 0; i < this.cartoonsInfo[k + 1].rectsAdd.length; i++) {
                this.cartoonRectR.push({
                  x: this.cartoonsInfo[k + 1].rateWidth * this.cartoonsInfo[k + 1].rectsAdd[i].x,
                  y: this.cartoonsInfo[k + 1].rateHeight * this.cartoonsInfo[k + 1].rectsAdd[i].y,
                  width: this.cartoonsInfo[k + 1].rateWidth * this.cartoonsInfo[k + 1].rectsAdd[i].width,
                  height: this.cartoonsInfo[k + 1].rateHeight * this.cartoonsInfo[k + 1].rectsAdd[i].height,
                  colour: this.cartoonsInfo[k + 1].colorAdd,
                });
              }
            }

            if (this.cartoonsInfo[k + 1].displayDel === '1000000601' && this.cartoonsInfo[k + 1].rectsDel != null) {
              for (let i = 0; i < this.cartoonsInfo[k + 1].rectsDel.length; i++) {
                this.cartoonRectR.push({
                  x: this.cartoonsInfo[k + 1].rateWidth * this.cartoonsInfo[k + 1].rectsDel[i].x,
                  y: this.cartoonsInfo[k + 1].rateHeight * this.cartoonsInfo[k + 1].rectsDel[i].y,
                  width: this.cartoonsInfo[k + 1].rateWidth * this.cartoonsInfo[k + 1].rectsDel[i].width,
                  height: this.cartoonsInfo[k + 1].rateHeight * this.cartoonsInfo[k + 1].rectsDel[i].height,
                  colour: this.cartoonsInfo[k + 1].colorDel,
                });
              }
            }
          }

          loadImageCanvas(url1, url2, this.cartoonRectL, this.cartoonRectR, this.power);
        }

      },

      previousImage() {
        if (this.orderCartoon > 0) {
          this.orderCartoon = this.orderCartoon - 1;
          this.loadImage();
        }
      },
      nextImage() {
        if (this.orderCartoon < this.cntCartoon - 1) {
          this.orderCartoon = this.orderCartoon + 1;
          this.loadImage();
        }
      },

      getHandGoodString(string) {
        if (string === '') return '';
        let handGood = string.split(",");
        let k = 0, handGoodStr = '';
        for (let j = 0; j < 5; j++) {
          if (handGood[0] === this.handGoodDataCode[j]) {
            handGoodStr = this.handGoodDataCodeValue[this.handGoodDataCode[j]].text;
          }
        }
        for (let i = 1; i < handGood.length; i++) {
          for (let j = 0; j < 5; j++) {
            if (handGood[i] === this.handGoodDataCode[j]) {
              handGoodStr += ',' + this.handGoodDataCodeValue[this.handGoodDataCode[j]].text;
            }
          }
        }
        return handGoodStr;
      },

      getOptionValue(dataCode) {
        const dictionary = {
          "1000000001": `${this.$t('permission-management.male')}`,
          "1000000002": `${this.$t('permission-management.female')}`,
          "1000000601": `${this.$t('system-setting.parameter-setting.yes')}`,
          "1000000602": `${this.$t('system-setting.parameter-setting.no')}`,
          "1000001701": `${this.$t('permission-management.timeout')}`,
          "1000001702": `${this.$t('permission-management.timein')}`,
          "TRUE": `${this.$t('knowledge-base.suspect')}`,
          "FALSE": `${this.$t('knowledge-base.no-suspect')}`,
          "1000001301": `${this.$t('permission-management.female')}`,
          "1000001302": `${this.$t('permission-management.female')}`,
          "1000001303": `${this.$t('maintenance-management.process-task.hand')}`,
          "1000001304": `${this.$t('maintenance-management.process-task.scan')}`,
          "1000001102": `${this.$t('maintenance-management.process-task.dispatch')}`,
          "1000001103": `${this.$t('maintenance-management.process-task.judge')}}`,
          "1000001104": `${this.$t('maintenance-management.process-task.hand')}`,
          "1000001106": `${this.$t('maintenance-management.process-task.scan')}`,
          "1000001201": `${this.$t('maintenance-management.process-task.system')}`,
          "1000001202": `${this.$t('maintenance-management.process-task.artificial')}`,
          "1000002301": `${this.$t('maintenance-management.process-task.system')}`,
          "1000002302": `${this.$t('maintenance-management.process-task.artificial')}`,
          "1000002303": `${this.$t('maintenance-management.process-task.artificial')}`,
          "1000001801": `${this.$t('maintenance-management.process-task.underreport')}`,
          "1000001802": `${this.$t('maintenance-management.process-task.falsepositive')}`,
          "0": `${this.$t('maintenance-management.process-task.online')}`,
          "1": `${this.$t('maintenance-management.process-task.offline')}`
        };
        if (!dictionary.hasOwnProperty(dataCode)) return '';
        return dictionary[dataCode];
      },

      getModeDataCode(value) {
        const dictionary = {

          "scan": `1000001301`,
          "scan+hand": `1000001302`,
          "scan+judge": `1000001303`,
          "all": `1000001304`,

        };
        if (!dictionary.hasOwnProperty(value)) return '';
        return dictionary[value];
      },

      getModeName(value) {
        const dictionary = {

          "1000001301": this.$t('personal-inspection.security-instrument'),
          "1000001302": this.$t('personal-inspection.security-instrument-and-hand-test'),
          "1000001303": this.$t('personal-inspection.security-instrument-and-manual-test'),
          "1000001304": this.$t('personal-inspection.security-instrument-and-hand-test-and-device'),

        };
        if (!dictionary.hasOwnProperty(value)) return '';
        return dictionary[value];
      },

      getSiteOption() {
        getApiManager()
          .post(`${apiBaseUrl}/site-management/field/get-all`).then((response) => {
          let message = response.data.message;
          let data = response.data.data;
          switch (message) {
            case responseMessages['ok']:
              this.siteData = data;
              break;
          }
        })
          .catch((error) => {
          });

      },
      onRowClicked: function (data) {

        this.pageStatus = 'show';
        this.power = false;
        this.isSlidebar1Expended = false;
        this.isSlidebar2Expended = false;
        this.cntCartoon = 0;
        this.orderCartoon = 0;
        this.judgeUserId = null;
        this.caseDealId = data.caseDealId;
        let url1 = '';
        let url2 = '';
        let rateWidth, rateHeight;
        let deviceImage, submitRects, cartoonRects;
        let colourInfo = null;
        let handGood = null, handAttached = null;

        // call api
        getApiManager()
          .post(`${apiBaseUrl}/task/process-task/get-one`, {
            'taskId': data.taskId,
          })
          .then((response) => {
            let message = response.data.message;

            switch (message) {
              case responseMessages['ok']:
                this.showPage = response.data.data;
                this.apiBaseURL = apiBaseUrl;

                //if(this.showPage.workFlow.modeName
                let modeName;
                this.judgeStartTime = null;
                this.judgeDeviceName = null;

                this.judgeUserName = null;
                this.handStartTime = null;
                this.handDeviceName = null;
                this.handUserName = null;



                for (let i = 0; i < this.showPage.serAssignList.length; i++) {
                  if (this.showPage.serAssignList[i].handDevice !== null) {
                    this.handDeviceName = this.showPage.serAssignList[i].handDevice.deviceName;
                    this.handUserName = this.showPage.serAssignList[i].assignUser.userName;
                    this.handStartTime = this.showPage.serAssignList[i].assignEndTime;
                  } else {
                    if (this.showPage.serAssignList[i].judgeDevice !== null) {
                      this.judgeDeviceName = this.showPage.serAssignList[i].judgeDevice.deviceName;
                    }
                    this.judgeUserName = this.showPage.serAssignList[i].assignUser.userName;
                    this.judgeStartTime = this.showPage.serAssignList[i].assignEndTime;
                    this.judgeUserId = this.showPage.serAssignList[i].assignUser.userId;
                  }
                }

                if (this.showPage.serJudgeGraph !== null) {
                  // this.judgeStartTime = this.showPage.serJudgeGraph.judgeStartTime;
                  // this.judgeDeviceName = this.showPage.serJudgeGraph.judgeDevice.deviceName;
                  this.judgeUserId = this.showPage.serJudgeGraph.judgeUserId;
                  this.judgeUserName = this.showPage.serJudgeGraph.judgeUser.userName;
                }


                this.thumbs = [];
                this.videos = [];
                this.images = [];
                this.imgRect = [];
                this.cartoonRect = [];
                this.rRects = [];
                this.imagesInfo = [];
                this.cartoonsInfo = [];
                this.imageRectR = [];
                this.imageRectL = [];
                this.cartoonRectR = [];
                this.cartoonRectL = [];
                this.collectionLabel = [];
                this.handGoodExpanded = [];
                this.handGoodDataCodeExpanded = [];

                // this.conclusionType = null;
                // if(this.judgeUserId===this.defaultUserId) {
                //   this.conclusionType = this.showPage.serCheckResultList[0].conclusionType;
                // }

                deviceImage=[];
                submitRects=[];
                colourInfo = this.showPage.platFormCheckParams;

                if(this.showPage.serScan!==undefined && this.showPage.serScan!==null) {
                  if(this.showPage.serScan.scanDeviceImages!== null) {
                    deviceImage = this.showPage.serScan.scanDeviceImages;
                    deviceImage = JSON.parse(deviceImage);
                  }
                }

                if (this.showPage.serJudgeGraph !== undefined && this.showPage.serJudgeGraph !== null) {
                  if(this.showPage.serJudgeGraph.judgeCartoonRects !== undefined && this.showPage.serJudgeGraph.judgeCartoonRects !== null) {
                    cartoonRects = this.showPage.serJudgeGraph.judgeCartoonRects;
                    cartoonRects = JSON.parse(cartoonRects);
                  }
                  if (this.showPage.serJudgeGraph.judgeSubmitrects !== undefined &&  this.showPage.serJudgeGraph.judgeSubmitrects !== null) {
                    submitRects = this.showPage.serJudgeGraph.judgeSubmitrects;
                    submitRects = JSON.parse(submitRects);
                  }
                }

                this.cntCartoon = Math.floor(deviceImage.length / 2);

                if (deviceImage !== null) {
                  for (let i = 0; i < deviceImage.length; i++) {
                    if (i < 2) {
                      this.imagesInfo.push({
                        rateWidth: deviceImage[i].width != 0 && deviceImage[i].width !=null ? 248 / deviceImage[i].width :0,
                        rateHeight: deviceImage[i].width != 0 && deviceImage[i].width !=null ? 521 / deviceImage[i].height :0,
                        imageUrl: deviceImage[i].cartoon,
                        imageRect: deviceImage[i].cartoonRects,
                        colorRect: colourInfo.scanRecogniseColour,
                        colorAdd: colourInfo.judgeRecogniseColour,
                        colorDel: colourInfo.displayDeleteSuspicionColour,
                        displayDel: colourInfo.displayDeleteSuspicion,
                        rectsAdd: cartoonRects !=null && cartoonRects[i] != undefined? cartoonRects[i].rectsAdded : null,
                        rectsDel: cartoonRects !=null && cartoonRects[i] != undefined?  cartoonRects[i].rectsDeleted : null
                      });
                    }


                      this.cartoonsInfo.push({
                        rateWidth: deviceImage[i].width != 0 && deviceImage[i].width !=null ? 205 / deviceImage[i].width :0,
                        rateHeight: deviceImage[i].width != 0 && deviceImage[i].width !=null ?  426 / deviceImage[i].height :0,
                        imageUrl: deviceImage[i].image,
                        imageRect: deviceImage[i].imageRects,
                        colorRect: colourInfo.scanRecogniseColour,
                        colorAdd: colourInfo.judgeRecogniseColour,
                        colorDel: colourInfo.displayDeleteSuspicionColour,
                        displayDel: colourInfo.displayDeleteSuspicion,
                        rectsAdd: submitRects !=null && submitRects[i] != undefined? submitRects[i].rectsAdded : null,
                        rectsDel: submitRects !=null && submitRects[i] != undefined?  submitRects[i].rectsDeleted : null
                      });

                  }
                }

                if (this.imagesInfo[0] !== undefined) {
                  if (this.imagesInfo[0].imageRect != null) {
                    if(this.imagesInfo[0].rectsDel != null){
                      for (let i = 0; i < this.imagesInfo[0].imageRect.length; i++) {
                        let isDeleted = false;
                        for (let j = 0; j < this.imagesInfo[0].rectsDel.length; j++) {
                          if(this.imagesInfo[0].imageRect[i].x === this.imagesInfo[0].rectsDel[j].x && this.imagesInfo[0].imageRect[i].y === this.imagesInfo[0].rectsDel[j].y && this.imagesInfo[0].imageRect[i].width === this.imagesInfo[0].rectsDel[j].width && this.imagesInfo[0].imageRect[i].height === this.imagesInfo[0].rectsDel[j].height) {
                            isDeleted = true;
                            break;
                          }
                        }
                        if(!isDeleted) {
                          this.imageRectL.push({
                            x: this.imagesInfo[0].rateWidth * this.imagesInfo[0].imageRect[i].x,
                            y: this.imagesInfo[0].rateHeight * this.imagesInfo[0].imageRect[i].y,
                            width: this.imagesInfo[0].rateWidth * this.imagesInfo[0].imageRect[i].width,
                            height: this.imagesInfo[0].rateHeight * this.imagesInfo[0].imageRect[i].height,
                            colour: this.imagesInfo[0].colorRect,
                          });
                        }
                      }
                      for (let i = 0; i < this.imagesInfo[0].rectsDel.length; i++) {
                        console.log(this.imagesInfo[0].rectsDel);
                        let isDeleted = false;
                        for (let j = 0; j < this.imagesInfo[0].imageRect.length; j++) {
                          if(this.imagesInfo[0].rectsDel[i].x === this.imagesInfo[0].imageRect[j].x && this.imagesInfo[0].rectsDel[i].y === this.imagesInfo[0].imageRect[j].y && this.imagesInfo[0].rectsDel[i].width === this.imagesInfo[0].imageRect[j].width && this.imagesInfo[0].rectsDel[i].height === this.imagesInfo[0].imageRect[j].height) {
                            isDeleted = true;
                            break;
                          }
                        }
                        if(!isDeleted) {
                          this.imagesInfo[0].rectsDel[i].x = 0;
                          this.imagesInfo[0].rectsDel[i].y = 0;
                          this.imagesInfo[0].rectsDel[i].width = 0;
                          this.imagesInfo[0].rectsDel[i].height = 0;
                        }
                      }
                    }
                    else {
                      for (let i = 0; i < this.imagesInfo[0].imageRect.length; i++) {
                        this.imageRectL.push({
                          x: this.imagesInfo[0].rateWidth * this.imagesInfo[0].imageRect[i].x,
                          y: this.imagesInfo[0].rateHeight * this.imagesInfo[0].imageRect[i].y,
                          width: this.imagesInfo[0].rateWidth * this.imagesInfo[0].imageRect[i].width,
                          height: this.imagesInfo[0].rateHeight * this.imagesInfo[0].imageRect[i].height,
                          colour: this.imagesInfo[0].colorRect,
                        });
                      }
                    }
                  }
                  url1 = this.imagesInfo[0].imageUrl;
                  if (this.imagesInfo[0].rectsAdd != null) {
                    for (let i = 0; i < this.imagesInfo[0].rectsAdd.length; i++) {

                      this.imageRectL.push({
                        x: this.imagesInfo[0].rateWidth * this.imagesInfo[0].rectsAdd[i].x,
                        y: this.imagesInfo[0].rateHeight * this.imagesInfo[0].rectsAdd[i].y,
                        width: this.imagesInfo[0].rateWidth * this.imagesInfo[0].rectsAdd[i].width,
                        height: this.imagesInfo[0].rateHeight * this.imagesInfo[0].rectsAdd[i].height,
                        colour: this.imagesInfo[0].colorAdd,
                      });
                    }
                  }

                  if (this.imagesInfo[0].displayDel === '1000000601') {
                    if (this.imagesInfo[0].rectsDel != null) {
                      for (let i = 0; i < this.imagesInfo[0].rectsDel.length; i++) {
                        this.imageRectL.push({
                          x: this.imagesInfo[0].rateWidth * this.imagesInfo[0].rectsDel[i].x,
                          y: this.imagesInfo[0].rateHeight * this.imagesInfo[0].rectsDel[i].y,
                          width: this.imagesInfo[0].rateWidth * this.imagesInfo[0].rectsDel[i].width,
                          height: this.imagesInfo[0].rateHeight * this.imagesInfo[0].rectsDel[i].height,
                          colour: this.imagesInfo[0].colorDel,
                        });
                      }
                    }
                  }
                }

                if (this.imagesInfo[1] !== undefined) {
                  if (this.imagesInfo[1].imageRect != null) {
                    if(this.imagesInfo[1].rectsDel != null){
                      for (let i = 0; i < this.imagesInfo[1].imageRect.length; i++) {
                        let isDeleted = false;
                        for (let j = 0; j < this.imagesInfo[1].rectsDel.length; j++) {
                          if(this.imagesInfo[1].imageRect[i].x === this.imagesInfo[1].rectsDel[j].x && this.imagesInfo[1].imageRect[i].y === this.imagesInfo[1].rectsDel[j].y && this.imagesInfo[1].imageRect[i].width === this.imagesInfo[1].rectsDel[j].width && this.imagesInfo[1].imageRect[i].height === this.imagesInfo[1].rectsDel[j].height) {
                            isDeleted = true;
                            break;
                          }
                        }
                        if(!isDeleted) {
                          this.imageRectR.push({
                            x: this.imagesInfo[1].rateWidth * this.imagesInfo[1].imageRect[i].x,
                            y: this.imagesInfo[1].rateHeight * this.imagesInfo[1].imageRect[i].y,
                            width: this.imagesInfo[1].rateWidth * this.imagesInfo[1].imageRect[i].width,
                            height: this.imagesInfo[1].rateHeight * this.imagesInfo[1].imageRect[i].height,
                            colour: this.imagesInfo[1].colorRect,
                          });
                        }
                      }
                      for (let i = 0; i < this.imagesInfo[1].rectsDel.length; i++) {
                        let isDeleted = false;
                        for (let j = 0; j < this.imagesInfo[1].imageRect.length; j++) {
                          if(this.imagesInfo[1].rectsDel[i].x === this.imagesInfo[1].imageRect[j].x && this.imagesInfo[1].rectsDel[i].y === this.imagesInfo[1].imageRect[j].y && this.imagesInfo[1].rectsDel[i].width === this.imagesInfo[1].imageRect[j].width && this.imagesInfo[1].rectsDel[i].height === this.imagesInfo[1].imageRect[j].height) {
                            isDeleted = true;
                            break;
                          }
                        }
                        if(!isDeleted) {
                          this.imagesInfo[1].rectsDel[i].x = 0;
                          this.imagesInfo[1].rectsDel[i].y = 0;
                          this.imagesInfo[1].rectsDel[i].width = 0;
                          this.imagesInfo[1].rectsDel[i].height = 0;
                        }
                      }
                    }
                    else {
                      for (let i = 0; i < this.imagesInfo[1].imageRect.length; i++) {
                        this.imageRectR.push({
                          x: this.imagesInfo[1].rateWidth * this.imagesInfo[1].imageRect[i].x,
                          y: this.imagesInfo[1].rateHeight * this.imagesInfo[1].imageRect[i].y,
                          width: this.imagesInfo[1].rateWidth * this.imagesInfo[1].imageRect[i].width,
                          height: this.imagesInfo[1].rateHeight * this.imagesInfo[1].imageRect[i].height,
                          colour: this.imagesInfo[1].colorRect,
                        });
                      }
                    }
                  }
                  url2 = this.imagesInfo[1].imageUrl;
                  if (this.imagesInfo[1].rectsAdd != null) {
                    for (let i = 0; i < this.imagesInfo[1].rectsAdd.length; i++) {
                      this.imageRectR.push({
                        x: this.imagesInfo[1].rateWidth * this.imagesInfo[1].rectsAdd[i].x,
                        y: this.imagesInfo[1].rateHeight * this.imagesInfo[1].rectsAdd[i].y,
                        width: this.imagesInfo[1].rateWidth * this.imagesInfo[1].rectsAdd[i].width,
                        height: this.imagesInfo[1].rateHeight * this.imagesInfo[1].rectsAdd[i].height,
                        colour: this.imagesInfo[1].colorAdd,
                      });
                    }
                  }

                  if (this.imagesInfo[1].displayDel === '1000000601' && this.imagesInfo[1].rectsDel != null) {
                    for (let i = 0; i < this.imagesInfo[1].rectsDel.length; i++) {
                      this.imageRectR.push({
                        x: this.imagesInfo[1].rateWidth * this.imagesInfo[1].rectsDel[i].x,
                        y: this.imagesInfo[1].rateHeight * this.imagesInfo[1].rectsDel[i].y,
                        width: this.imagesInfo[1].rateWidth * this.imagesInfo[1].rectsDel[i].width,
                        height: this.imagesInfo[1].rateHeight * this.imagesInfo[1].rectsDel[i].height,
                        colour: this.imagesInfo[1].colorDel,
                      });
                    }
                  }
                }

                loadImageCanvas(url1, url2, this.imageRectL, this.imageRectR, this.power);

                let handGoodsStr = this.showPage.serCheckResult.handGoods;
                let handAttactedStr = this.showPage.serCheckResult.handAttached;
                if (handGoodsStr !== null) {
                  handGood = handGoodsStr.split(",");
                }

                if (handAttactedStr !== null) {
                  handAttached = handAttactedStr.split(",");
                }
                let k = 0;
                if(handGood!==null) {
                  for (let i = 0; i < handGood.length; i++) {
                    for (let j = 0; j < 5; j++) {
                      if (handGood[i] === this.handGoodDataCode[j]) {
                        this.handGoodExpanded[k] = true;
                        this.handGoodDataCodeExpanded[k] = this.handGoodDataCode[j];
                        k++;
                      }
                    }
                  }
                }

                //getting media data from server.
                if(handAttached !==null) {
                  for (let i = 0; i < handAttached.length; i++) {
                    let iHandAttached = handAttached[i].split(".");
                    if (iHandAttached[1] === "png" || iHandAttached[1] === "jpg") {
                      this.thumbs.push({
                        name: iHandAttached[0],
                        src: handAttached[i]
                      });
                      this.images.push(handAttached[i]);
                    } else {
                      this.videos.push({
                        name: iHandAttached[0],
                        src: handAttached[i],
                        poster: '',//todo if client need to show different poster for each videos, should get its poster image from server.
                      });
                    }
                  }
                }
                break;// okay

            }
          })
          .catch((error) => {
          });
      },

      getDateTimeFormat2(dataTime) {
        if (dataTime == null) {
          return '';
        }
        return getDateTimeWithFormat(dataTime);
      },

      getDateTimeFormat(dataTime) {
        if (dataTime == null) {
          return '';
        }
        return getDateTimeWithFormat(dataTime, 'monitor');
      },

      onSearchButton() {
        this.$refs.pendingListTable.refresh();
      },

      onResetButton() {
        this.filter = {
          taskNumber: null,
          caseStatus: "1000002503",
          taskResult: null,
          fieldId: null,
          fieldDesignation: null,
          handGoods: null,
        };
      },

      onExportButton() {
        // this.fileSelection = [];
        // this.$refs['model-export'].show();
        let checkedAll = this.$refs.pendingListTable.checkedAllStatus;
        let checkedIds = this.$refs.pendingListTable.selectedTo;
        let httpOption = this.$refs.pendingListTable.httpOptions;
        this.params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'locale' : getLocale(),
          'sort' : httpOption.params.sort,
          'filter': this.filter,
          'idList': checkedIds.join()
        };
        this.link = `knowledge-base/generate/personal`;
        this.imgUrl = `knowledge-base/generate/image`;
        this.name = 'Knowledge-Personal';

        this.isModalVisible = true;
      },
      onExport(){
        let checkedAll = this.$refs.pendingListTable.checkedAllStatus;
        let checkedIds = this.$refs.pendingListTable.selectedTo;
        let params = {
          'locale' : getLocale(),
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': this.filter,
          'idList': checkedIds.join()
        };
        let link = `knowledge-base/generate/personal`;
        if (checkedIds.length > 0) {
          downLoadFileFromServer(link, params, 'Knowledge-Personal', this.fileSelection);
        this.hideModal('model-export')
        }
      },

      hideModal(modal) {
        this.$refs[modal].hide();
      },


      onPrintButton() {
        let checkedAll = this.$refs.pendingListTable.checkedAllStatus;
        let checkedIds = this.$refs.pendingListTable.selectedTo;
        let httpOption = this.$refs.pendingListTable.httpOptions;
        let params = {
          'locale' : getLocale(),
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'sort' : httpOption.params.sort,
          'filter': this.filter,
          'idList': checkedIds.join()
        };
        let link = `knowledge-base/generate/personal`;

          printFileFromServer(link, params);

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
        let temp;
        let idTemp;
        for (let i = 0; i < data.data.length; i++) {
          temp = data.data[i];
          this.renderedCheckList.push(data.data[i].caseDealId);
          transformed.data.push(temp);
          this.idList.push(idTemp);
          if (this.isCheckAll === true) {
            this.$refs.pendingListTable.selectedTo.push(idTemp);
          }
        }

        return transformed

      },

      pendingListTableHttpFetch(apiUrl, httpOptions) { // customize data loading for table from server

        this.renderedCheckList = [];

        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.pendingListTableItems.perPage,
          sort: httpOptions.params.sort,
          filter: this.filter,
        });
      },
      onBlackListTablePaginationData(paginationData) {
        this.$refs.pendingListTablePagination.setPaginationData(paginationData);
        this.changeCheckAllStatus();
      },
      onBlackListTableChangePage(page) {
        this.$refs.pendingListTable.changePage(page);
        this.changeCheckAllStatus();
      },

      showModal(data) {
        this.caseDealId= data;
        this.$refs['modal-dismiss'].show();
      },

      onAction(data) { // called when any action button is called from table
        // call api
        getApiManager()
          .post(`${apiBaseUrl}/knowledge-base/delete`, {
            'caseDealId': data,
          })
          .then((response) => {
            let message = response.data.message;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`personal-inspection.dismiss`), {
                  duration: 3000,
                  permanent: false
                });
                this.$refs['modal-dismiss'].hide();
                this.pageStatus= 'table';
                this.$refs.pendingListTable.refresh();
                break;

            }
          })
          .catch((error) => {
          });

      },
    }
  }
</script>


