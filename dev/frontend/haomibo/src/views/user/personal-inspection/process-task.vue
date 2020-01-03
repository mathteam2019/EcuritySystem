<template>
  <div>
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb/>
        </b-colxx>
      </b-row>
    </div>
    <b-card class="main-without-tab" v-if="pageStatus === 'table'" style="margin-top: 20px;">
      <div class="h-100 d-flex flex-column">
        <b-row class="pt-2">
          <b-col cols="8">
            <b-row>

              <b-col>
                <b-form-group :label="$t('personal-inspection.task-number')">
                  <b-form-input v-model="filter.taskNumber"/>
                </b-form-group>
              </b-col>

              <b-col>
                <b-form-group :label="$t('personal-inspection.operation-mode')">
                  <b-form-select v-model="filter.mode" :options="operationModeOptions" plain/>
                </b-form-group>
              </b-col>

              <b-col>
                <b-form-group :label="$t('personal-inspection.status')">
                  <b-form-select v-model="filter.status" :options="statusOptions" plain/>
                </b-form-group>
              </b-col>

              <b-col>
                <b-form-group :label="$t('personal-inspection.on-site')">
                  <b-form-select v-model="filter.fieldId" :options="onSiteOption" plain/>
                </b-form-group>
              </b-col>

              <b-col class="d-flex align-items-center" style="padding-top: 10px;">
                      <span class="rounded-span flex-grow-0 text-center text-light" @click="isExpanded = !isExpanded">
                        <i :class="!isExpanded?'icofont-rounded-down':'icofont-rounded-up'"/>
                      </span>
              </b-col>
            </b-row>
          </b-col>
          <b-col cols="8" v-if="isExpanded">
            <b-row>

              <b-col>
                <b-form-group :label="$t('personal-inspection.user')">
                  <b-form-input v-model="filter.userName"/>
                </b-form-group>
              </b-col>

              <b-col>
                <b-form-group :label="$t('log-management.operating-log.start-time')">
                  <date-picker v-model="filter.startTime" type="datetime" format="YYYY-MM-DD HH:mm"
                               placeholder=""/>
                </b-form-group>
              </b-col>

              <b-col>
                <b-form-group :label="$t('log-management.operating-log.end-time')">
                  <date-picker v-model="filter.endTime" type="datetime" format="YYYY-MM-DD HH:mm"
                               placeholder=""/>
                </b-form-group>
              </b-col>
              <b-col/>
              <b-col/>
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
              <b-button size="sm" class="ml-2" variant="outline-info default" :disabled="checkPermItem('process_task_export')" @click="onExportButton()">
                <i class="icofont-share-alt"/>&nbsp;{{ $t('log-management.export')}}
              </b-button>
              <b-button size="sm" class="ml-2" variant="outline-info default" :disabled="checkPermItem('process_task_print')" @click="onPrintButton()">
                <i class="icofont-printer"/>&nbsp;{{ $t('log-management.print') }}
              </b-button>
            </div>
          </b-col>
        </b-row>

        <b-row class="flex-grow-1">
          <b-col cols="12">
            <div class="table-wrapper table-responsive">
              <vuetable
                ref="taskVuetable"
                track-by="taskId"
                :api-url="taskVuetableItems.apiUrl"
                :fields="taskVuetableItems.fields"
                :http-fetch="taskVuetableHttpFetch"
                :per-page="taskVuetableItems.perPage"
                pagination-path="pagination"
                class="table-hover"
                @vuetable:pagination-data="onTaskVuetablePaginationData"
              >
                <template slot="taskNumber" slot-scope="props">
                    <span class="cursor-p text-primary" @click="onRowClicked(props.rowData.taskId)">
                      {{props.rowData.taskNumber}}
                    </span>
                </template>
                <template slot="mode" slot-scope="props">
                  <div v-if="props.rowData.workFlow==null"></div>
                  <div v-else-if="props.rowData.workFlow.workMode==null"></div>
                  <div v-else>
                    <div v-if="props.rowData.workFlow.workMode.modeName===getModeDataCode('all')">
                      <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                      <b-img src="/assets/img/monitors_icon.svg" class="operation-icon"/>
                      <b-img src="/assets/img/mobile_icon.svg" class="operation-icon"/>
                    </div>
                    <div v-if="props.rowData.workFlow.workMode.modeName===getModeDataCode('scan')">
                      <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                    </div>
                    <div v-if="props.rowData.workFlow.workMode.modeName===getModeDataCode('scan+judge')">
                      <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                      <b-img src="/assets/img/monitors_icon.svg" class="operation-icon"/>
                    </div>
                    <div v-if="props.rowData.workFlow.workMode.modeName===getModeDataCode('scan+hand')">
                      <b-img src="/assets/img/man_scan_icon.svg" class="operation-icon"/>
                      <b-img src="/assets/img/mobile_icon.svg" class="operation-icon"/>
                    </div>
                  </div>
                </template>
              </vuetable>
            </div>
            <div class="pagination-wrapper">
              <vuetable-pagination-bootstrap
                ref="taskVuetablePagination"
                @vuetable-pagination:change-page="onTaskVuetableChangePage"
                :initial-per-page="taskVuetableItems.perPage"
                @onUpdatePerPage="taskVuetableItems.perPage = Number($event)"
              />
            </div>
          </b-col>
        </b-row>
      </div>
    </b-card>

    <div v-if="pageStatus === 'show'">
      <b-row class="fill-main">
        <b-col cols="3">
          <b-card class="pt-4 h-100">
            <b-row class="mb-1">
              <b-col>
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
              <b-col class="text-right icon-container">
                <span v-if="showPage.serKnowledgeCase!=null && showPage.serKnowledgeCase.caseId!=null"><i class="icofont-star"/></span>
                <span v-if="showPage.serJudgeGraph!=null && showPage.serJudgeGraph.judgeResult==='1000001201'"><i class="icofont-search-user"/></span>
                <span v-if="showPage.serScan!=null && showPage.serScan.scanImageGender==='1000000002'"><i class="icofont-female"/></span>
              </b-col>
            </b-row>

            <b-row class="mb-4">
              <b-col>
                <canvas id="firstcanvas" class="img-fluid w-100"/>
              </b-col>
              <b-col>
                <canvas id="secondcanvas" class="img-fluid w-100"/>
              </b-col>
            </b-row>

            <b-row class="mb-2">
              <b-col class="control-group">
                <div class="control-btn-wrapper">

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
                    <b-img src="/assets/img/enhance_btn.png" @click="filterId(0)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.enhance')}}1</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/enhance_btn.png" @click="filterId(9)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.enhance')}}2</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/enhance_btn.png" @click="filterId(10)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.enhance')}}3</span>
                  </div>

                  <div class="control-btn">
                    <b-img src="/assets/img/edge_btn.png" @click="filterId(13)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.edge')}}</span>
                  </div>


                  <div class="control-btn">
                    <b-img src="/assets/img/reduction_btn.png" v-if="this.power === false"
                           @click="loadImage(imageUrl, imageUrl)"/>
                    <b-img src="/assets/img/reduction_btn.png" v-else
                           @click="loadImage(cartoonUrl, cartoonUrl)"/>
                    <span class="text-info text-extra-small">{{$t('personal-inspection.reduction')}}</span>
                  </div>
                </div>

                <div class="switch-wrapper">
                  <div class="separator"></div>
                  <div class="switch">
                    <switches v-model="power" :disabled="checkPermItem('process_task_toggle')" theme="custom" color="info"/>
                  </div>
                </div>
              </b-col>
              <b-col cols="8" v-if="isSlidebar2Expended" style="max-width: 100%; flex: none;">
                <VueSlideBar
                  v-model="slidebar2value"
                  :min="-50"
                  :max="50"
                  :processStyle="slider.processStyle"
                  :lineHeight="slider.lineHeight"
                  :tooltipStyles="{ backgroundColor: 'blue', borderColor: 'blue' }"
                  class="slide-class">
                </VueSlideBar>
              </b-col>
              <b-col cols="8" v-if="isSlidebar1Expended" style="max-width: 100%; flex: none;">
                <VueSlideBar
                  v-model="slidebar1value"
                  :min="-50"
                  :max="50"
                  :processStyle="slider.processStyle"
                  :lineHeight="slider.lineHeight"
                  :tooltipStyles="{ backgroundColor: 'blue', borderColor: 'blue' }"
                  class="slide-class">
                </VueSlideBar>
              </b-col>
              <b-col cols="8" v-if="isSlidebar3Expended" style="max-width: 100%; flex: none;">
                <VueSlideBar
                  v-model="slidebar3value"
                  :min="-50"
                  :max="50"
                  :processStyle="slider.processStyle"
                  :lineHeight="slider.lineHeight"
                  :tooltipStyles="{ backgroundColor: 'blue', borderColor: 'blue' }"
                  class="slide-class">
                </VueSlideBar>
              </b-col>
              <b-col cols="8" v-if="isSlidebar4Expended" style="max-width: 100%; flex: none;">
                <VueSlideBar
                  v-model="slidebar4value"
                  :min="-50"
                  :max="50"
                  :processStyle="slider.processStyle"
                  :lineHeight="slider.lineHeight"
                  :tooltipStyles="{ backgroundColor: 'blue', borderColor: 'blue' }"
                  class="slide-class">
                </VueSlideBar>
              </b-col>
            </b-row>
          </b-card>
        </b-col>
        <b-col cols="9">
          <b-card class="h-100 d-flex flex-column right-card">
            <div class="history-chart">
              <div>

                <div class="part">
                  <div class="left">
                    <div>{{$t('menu.start')}}</div>
                  </div>
                  <div class="right">
                    <div>Start</div>
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
                  <div class="right">
                    <div>Scanning</div>
                  </div>
                  <div class="top-date">
                    <label
                      v-if="showPage.serScan != null">{{this.getDateTimeFormat2(showPage.serScan.scanStartTime)}}</label>
                    <label v-else></label>
                  </div>
                  <div class="bottom-date">
                    <label
                      v-if="showPage.serScan != null">{{this.getDateTimeFormat2(showPage.serScan.scanEndTime)}}</label>
                    <label v-else></label>
                  </div>
                </div>

                <div class="part">
                  <div class="left">
                    <div>{{$t('maintenance-management.process-task.judge')}}</div>
                    <div>
                      <div v-if="showPage.serJudgeGraph == null"></div>
                      <div v-else-if="showPage.serJudgeGraph.judgeUser == null"></div>
                      <div v-else>{{showPage.serJudgeGraph.judgeUser.userName}}</div>
                    </div>
                  </div>
                  <div class="right">
                    <div>Decision</div>
                    <div>diagram</div>
                  </div>
                  <div class="top-date">
                    <label v-if="showPage.workFlow.workMode.modeName===getModeDataCode('scan+judge') || showPage.workFlow.workMode.modeName===getModeDataCode('all')">{{this.getDateTimeFormat2(showPage.serJudgeGraph.judgeStartTime)}}</label>
                    <label v-else></label>
                  </div>
                  <div class="bottom-date">
                    <label v-if="showPage.workFlow.workMode.modeName===getModeDataCode('scan+judge') || showPage.workFlow.workMode.modeName===getModeDataCode('all')">{{this.getDateTimeFormat2(showPage.serJudgeGraph.judgeEndTime)}}</label>
                    <label v-else></label>
                  </div>
                </div>

                <div class="part">
                  <div class="left">
                    <div>{{$t('device-config.maintenance-config.inspection')}}</div>
                    <div>
                      <div v-if="showPage.serHandExamination == null"></div>
                      <div v-else-if="showPage.serHandExamination.handUser == null"></div>
                      <div v-else>{{showPage.serHandExamination.handUser.userName}}</div>
                    </div>
                  </div>
                  <div class="right">
                    <div>Inspection</div>
                  </div>
                  <div class="top-date">
                    <label v-if="showPage.serHandExamination == null"></label>
                    <label v-else-if="showPage.workFlow.workMode.modeName===getModeDataCode('scan+hand') || showPage.workFlow.workMode.modeName===getModeDataCode('all')">{{this.getDateTimeFormat2(showPage.serHandExamination.handStartTime)}}</label>
                  </div>
                  <div class="bottom-date">
                    <label v-if="showPage.serHandExamination == null"></label>
                    <label v-else-if="showPage.workFlow.workMode.modeName===getModeDataCode('scan+hand') || showPage.workFlow.workMode.modeName===getModeDataCode('all')">{{this.getDateTimeFormat2(showPage.serHandExamination.handEndTime)}}</label>
                  </div>
                </div>

                <div class="part">
                  <div class="left">
                    <div>结束</div>
                  </div>
                  <div class="right">
                    <div>End</div>
                  </div>
                </div>

              </div>

            </div>

            <b-row>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.task-number')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label>{{showPage.taskNumber}}</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.on-site')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label v-if="showPage.field == null"></label>
                  <label v-else>{{showPage.field.fieldDesignation}}</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.security-instrument')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label v-if="showPage.serScan == null"></label>
                  <label v-else-if="showPage.serScan.scanDevice == null"></label>
                  <label v-else>{{showPage.serScan.scanDevice.deviceName}}</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.image-gender')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label v-if="showPage.serScan == null"></label>
                  <label v-else>{{getOptionValue(showPage.serScan.scanImageGender)}}</label>
                </b-form-group>
              </b-col>
            </b-row>

            <b-row>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.hand-check-station')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label v-if="showPage.serHandExamination == null"></label>
                  <label v-else-if="showPage.serHandExamination.handDevice == null"></label>
                  <label v-else>{{showPage.serHandExamination.handDevice.deviceName}}</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.judgement-station')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label v-if="showPage.serJudgeGraph == null"></label>
                  <label v-else-if="showPage.serJudgeGraph.judgeDevice == null"></label>
                  <label v-else>{{showPage.serJudgeGraph.judgeDevice.deviceName}}</label>
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.operation-mode')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
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
                </b-form-group>
              </b-col>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.judgement-conclusion-type')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label v-if="showPage.serJudgeGraph == null"></label>
                  <label v-else>{{getOptionValue(showPage.serJudgeGraph.judgeResult)}}</label>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    {{$t('personal-inspection.evaluation-chart')}}&nbsp
                    <span class="text-danger">*</span>
                  </template>
                  <label v-if="showPage.history == null"></label>
                  <label v-else>{{getOptionValue(showPage.history.handAppraise)}}</label>
                </b-form-group>
              </b-col>
              <b-col>
              </b-col>
              <b-col>
              </b-col>
              <b-col>
              </b-col>
            </b-row>
            <b-row>
              <b-col>
                <b-form-group>
                  <template slot="label">
                    备注
                    <span class="text-danger">*</span>
                  </template>
                  <label v-if="showPage.note == null"></label>
                  <label v-else>{{showPage.note}}</label>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col>
                <label class="font-weight-bold">{{$t('personal-inspection.seized-contraband')}}</label>
                <b-row class="justify-content-start" style="margin-bottom: 1rem; margin-top: 0.5rem">
                  <b-col>
                    <div v-if="handGoodExpanded[0]" class="text-center"  style="background-color: #ff0000; padding-top: 8px; padding-bottom: 8px; border-radius: 17px">
                      <span>{{handGoodDataCodeValue[handGoodDataCodeExpanded[0]].text}}</span>
                    </div>
                  </b-col>
                  <b-col>
                    <div v-if="handGoodExpanded[1]" class="text-center" style="background-color: #ff4e00; padding-top: 8px; padding-bottom: 8px; border-radius: 17px">
                      <span>{{handGoodDataCodeValue[handGoodDataCodeExpanded[1]].text}}</span>
                    </div>
                  </b-col>
                  <b-col>
                    <div v-if="handGoodExpanded[2]" class="text-center" style="background-color: #ff7e00; padding-top: 8px; padding-bottom: 8px; border-radius: 17px">
                      <span>{{handGoodDataCodeValue[handGoodDataCodeExpanded[2]].text}}</span>
                    </div>
                  </b-col>
                  <b-col>
                    <div v-if="handGoodExpanded[3]" class="text-center" style="background-color: #ffae00; padding-top: 8px; padding-bottom: 8px; border-radius: 17px">
                      <span>{{handGoodDataCodeValue[handGoodDataCodeExpanded[3]].text}}</span>
                    </div>
                  </b-col>
                  <b-col>
                    <div v-if="handGoodExpanded[4]" class="text-center" style="background-color: #ffae00; padding-top: 8px; padding-bottom: 8px; border-radius: 17px">
                      <span>{{handGoodDataCodeValue[handGoodDataCodeExpanded[4]].text}}</span>
                    </div>
                  </b-col>
                </b-row>

                <label class="font-weight-bold">{{$t('personal-inspection.obtained-evidence')}}</label>
                <b-row class="evidence-gallery" style="margin-top: 0.5rem">
                  <b-col cols="auto" v-for="(thumb, thumbIndex) in thumbs" :key="`thumb_${thumbIndex}`"
                         @click="onThumbClick(thumbIndex)">
                    <img :src="thumb.src" style="width: 50px; height: 40px;" :alt="thumb.name"/>

                  </b-col>
                  <b-col cols="auto" v-for="(video, videoIndex) in videos" :key="`video_${videoIndex}`"
                         @click="onVideoClick(video)">
                    <video style=" width: 50px; height: 40px;">
                      <source :src="video.src" type="video/mp4">
                    </video>
                  </b-col>
                  <light-gallery :images="images" :index="photoIndex" :disable-scroll="true" @close="handleHide()"/>

                </b-row>
              </b-col>
              <b-col style="max-width: 45%;">
                <b-row>
                  <b-col cols="12" class="align-self-end text-right mt-3">
                    <b-img v-if="validIcon === 'TRUE'" src="/assets/img/icon_invalid.png" class="align-self-end" style="width: 100px; height: 95px;"/>
                    <b-img v-else src="/assets/img/icon_valid.png" class="align-self-end" style="width: 100px; height: 95px;"/>

                  </b-col>
                </b-row>
                <b-row style="margin-top: 2rem">
                  <b-col cols="12" class="align-self-end text-right mt-3">
                    <b-button size="sm" class="ml-2" variant="info default" @click="onRowClicked(history_id)">
                      <i class="icofont-ui-reply"/>&nbsp;{{$t('log-management.refresh') }}
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
          <video-player ref="videoPlayer" :options="videoOptions"></video-player>
        </div>
        <span class="switch-action" @click="finishVideoShow()"><i class="icofont-close-line"></i></span>
      </div>
    </div>
  </div>
</template>

<style lang="scss">
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
        margin-bottom: 24px;

        img {
          $size: 40px;
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
  import VuetablePaginationBootstrap from "../../../components/Common/VuetablePaginationBootstrap";
  import {getApiManager, getDateTimeWithFormat, downLoadFileFromServer, printFileFromServer} from '../../../api';
  import {responseMessages} from '../../../constants/response-messages';
  import 'vue-tree-halower/dist/halower-tree.min.css' // you can customize the style of the tree
  import Switches from 'vue-switches';
  import {LightGallery} from 'vue-light-gallery';
  import DatePicker from 'vue2-datepicker';
  import 'vue2-datepicker/index.css';
  import 'vue2-datepicker/locale/zh-cn';
  import {loadImageCanvas, imageFilterById} from '../../../utils'
  import VueSlideBar from 'vue-slide-bar'
  import {checkPermissionItem} from "../../../utils";
  import Videoplayer from '../../../components/Common/VideoPlayer';


  const {required, email, minLength, maxLength, alphaNum} = require('vuelidate/lib/validators');

  export default {
    components: {
      'vuetable': Vuetable,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
      'switches': Switches,
      'light-gallery': LightGallery,
      'date-picker': DatePicker,
      'video-player': Videoplayer,
      VueSlideBar
    },
    mounted() {
      this.getSiteOption();
    },
    data() {

      return {
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
        validIcon:null,
        isSlidebar1Expended:false,
        isSlidebar2Expended:false,
        isSlidebar3Expended:false,
        isSlidebar4Expended:false,
        slidebar1value:0,
        slidebar2value:0,
        slidebar3value:0,
        slidebar4value:0,
        history_id:0,
        slider: {
          lineHeight: 10,
          processStyle: {
            backgroundColor: 'blue'
          }
        },
        isCheckAll: false,
        pageStatus: 'table',
        apiBaseURL: '',
        filter: {
          taskNumber: null,
          mode: null,
          status: null,
          fieldId: null,
          userName: null,
          startTime: null,
          endTime: null
          // TODO: search filter
        },

        siteData: [],
        showPage: [],

        imageUrls : [],
        imageUrl: null,
        cartoonUrl: null,
        handGoodDataCode:['1000001601', '1000001602', '1000001603', '1000001604', '1000001605'],
        handGoodExpanded:[false, false, false, false, false],
        handGoodDataCodeExpanded:[],
        handGoodDataCodeValue:{
          1000001601:{text:'安眠药'},
          1000001602:{text:'仿真枪'},
          1000001603:{text:'玩具枪'},
          1000001604:{text:'气枪'},
          1000001605:{text:'打火机'},
        },
        // TODO: select options
        operationModeOptions: [
          {value: null, text: this.$t('personal-inspection.all')},
          {value: '4', text: '安检仪+审图端+手检端'},
          {value: '1', text: '安检仪+(本地手检)'},
          {value: '2', text: '安检仪+手检端'},
          {value: '3', text: '安检仪+审图端'},
        ],
        statusOptions: [
          {value: null, text: this.$t('personal-inspection.all')},
          {value: '1000001102', text: this.$t('maintenance-management.process-task.dispatch')},
          {value: '1000001103', text: this.$t('maintenance-management.process-task.judge')},
          {value: '1000001104', text: this.$t('maintenance-management.process-task.hand')},
          {value: '1000001106', text: this.$t('maintenance-management.process-task.scan')}
        ],

        onSiteOption: [],
        taskVuetableItems: {
          apiUrl: `${apiBaseUrl}/task/process-task/get-by-filter-and-page`,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'taskId',
              title: this.$t('personal-inspection.serial-number'),
              sortField: 'taskId',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:taskNumber',
              sortField: 'taskNumber',
              title: this.$t('personal-inspection.task-number'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:mode',
              title: this.$t('personal-inspection.operation-mode'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'taskStatus',
              title: this.$t('personal-inspection.status'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (value) => {
                const dictionary = {
                  "1000001102": `<span>${this.$t('maintenance-management.process-task.dispatch')}</span>`,
                  "1000001103": `<span>${this.$t('maintenance-management.process-task.judge')}</span>`,
                  "1000001104": `<span>${this.$t('maintenance-management.process-task.hand')}</span>`,
                  "1000001106": `<span>${this.$t('maintenance-management.process-task.scan')}</span>`

                };
                if (!dictionary.hasOwnProperty(value)) return '';
                return dictionary[value];
              }
            },
            {
              name: 'field',
              title: this.$t('personal-inspection.on-site'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (field) => {
                if (field == null) return '';
                return field.fieldDesignation;
              }
            },
            {
              name: 'serScan',
              title: this.$t('personal-inspection.security-instrument'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (serScan) => {
                if (serScan == null) return '';
                if (serScan.scanDevice == null) return '';
                return serScan.scanDevice.deviceName;
              }
            },
            {
              name: 'serScan',
              title: this.$t('personal-inspection.guide'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (serScan) => {
                if (serScan == null) return '';
                if (serScan.scanPointsman == null) return '';
                return serScan.scanPointsman.userName;
              }
            },
            {
              name: 'serScan',
              title: this.$t('personal-inspection.scan-start-time'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (serScan) => {
                if (serScan == null) return '';
                return getDateTimeWithFormat(serScan.scanStartTime);
              }
            },
            {
              name: 'serScan',
              title: this.$t('personal-inspection.scan-end-time'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (serScan) => {
                if (serScan == null) return '';
                return getDateTimeWithFormat(serScan.scanEndTime);
              }
            },
          ],
          perPage: 10,
        },
        power: false,

        thumbs: [],
        images: [],
        videos: [],
        photoIndex: null,
        showVideo: false,

        widthRate:[],
        heightRate:[],
        imgRect:[],
        cartoonRect:[],
        rRects:[],
        rectAdd:[],
        rectDel:[],
        modal_video_url:"",
      }
    },
    watch: {
      'taskVuetableItems.perPage': function (newVal) {
        this.$refs.taskVuetable.refresh();
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
        //called whenever switch1 changes
        let url1;
        let url2;
        if (newValue === true) {
          url1 = this.cartoonUrl;
          url2 = this.cartoonUrl;
          loadImageCanvas(url1, url2, this.cartoonRect, this.rRects, true);

        } else {
          url1 = this.imageUrl;
          url2 = this.imageUrl;
          loadImageCanvas(url1, url2, this.imgRect, this.rRects, false);
        }
        this.isSlidebar3Expended = false;
        this.isSlidebar4Expended = false;
        this.isSlidebar1Expended = false;
        this.isSlidebar2Expended = false;

      },
      slidebar1value(newsValue, oldValue) {

        if(oldValue<newsValue) {
          for(let i=oldValue; i<newsValue; i++) {
            this.filterId(5);
          }
        }
        else {
          for(let i=newsValue; i<oldValue; i++) {
            this.filterId(6);
          }
        }
      },
      slidebar2value(newsValue, oldValue) {

        if(oldValue<newsValue) {
          for(let i=oldValue; i<newsValue; i++) {
            this.filterId(7);
          }
        }
        else {
          for(let i=newsValue; i<oldValue; i++) {
            this.filterId(8);
          }
        }
      },
      slidebar3value(newsValue, oldValue) {

        if(oldValue<newsValue) {
          for(let i=oldValue; i<newsValue; i++) {
            this.filterId(5);
          }
        }
        else {
          for(let i=newsValue; i<oldValue; i++) {
            this.filterId(6);
          }
        }
      },
      slidebar4value(newsValue, oldValue) {

        if(oldValue<newsValue) {
          for(let i=oldValue; i<newsValue; i++) {
            this.filterId(7);
          }
        }
        else {
          for(let i=newsValue; i<oldValue; i++) {
            this.filterId(8);
          }
        }
      },
    },
    methods: {
    checkPermItem(value) {
        return checkPermissionItem(value);
      },
      onVideoClick(video){
        this.videoOptions.sources.src = video.src;
        this.$refs.videoPlayer.initialize();
        this.showVideo = true;
      },
      finishVideoShow(){
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
        if (this.power === false) {
          this.isSlidebar3Expended = false;
          this.isSlidebar4Expended = false;
          if (value === 1) {
            this.isSlidebar1Expended = !this.isSlidebar1Expended;
            this.isSlidebar2Expended = !this.isSlidebar1Expended;
          }
          if (value === 2) {
            this.isSlidebar2Expended = !this.isSlidebar2Expended;
            this.isSlidebar1Expended = !this.isSlidebar2Expended;
          }
        }
        else {
          this.isSlidebar1Expended= false;
          this.isSlidebar2Expended =false;
          if (value === 1) {
            this.isSlidebar3Expended = !this.isSlidebar3Expended;
            this.isSlidebar4Expended = !this.isSlidebar3Expended;
          }
          if (value === 2) {
            this.isSlidebar4Expended = !this.isSlidebar4Expended;
            this.isSlidebar3Expended = !this.isSlidebar4Expended;
          }
        }
      },

      filterId(id) {
        if(id<5||id>8) {
          this.isSlidebar1Expended = false;
          this.isSlidebar2Expended = false;
          this.isSlidebar3Expended = false;
          this.isSlidebar4Expended = false;
          // if(this.power === false){

          //   loadImageCanvas(this.imageUrls[0], this.imageUrls[1]);
          // } else{
          //   loadImageCanvas(this.imageUrls[3], this.imageUrls[4]);
          // }
        }
        if(this.power===false) {
          imageFilterById(id, this.imgRect, this.rRects);
        }else {
          imageFilterById(id, this.cartoonRect, this.rRects);
        }

      },

      loadImage(url1, url2) {
        if(this.power===false) {
          this.slidebar1value = 0;
          this.slidebar2value = 0;
          loadImageCanvas(url1, url2, this.imgRect, this.rRects, this.power);
        }else {
          this.slidebar3value = 0;
          this.slidebar4value = 0;
          loadImageCanvas(url1, url2, this.cartoonRect, this.rRects, this.power);
        }

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
          "1000001801": `${this.$t('maintenance-management.process-task.underreport')}`,
          "1000001802": `${this.$t('maintenance-management.process-task.falsepositive')}`
        };
        if (!dictionary.hasOwnProperty(dataCode)) return '';
        return dictionary[dataCode];
      },

      getModeDataCode(value){
        const dictionary = {

          "scan": `1000001301`,
          "scan+hand": `1000001302`,
          "scan+judge": `1000001303`,
          "all": `1000001304`,

        };
        if (!dictionary.hasOwnProperty(value)) return '';
        return dictionary[value];
      },

      onExportButton() {
        let checkedAll = this.$refs.taskVuetable.checkedAllStatus;
        let checkedIds = this.$refs.taskVuetable.selectedTo;
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : false,
          'filter': this.filter,
          'idList': checkedIds.join()
        };
        let link = `task/process-task/generate`;
        if(checkedIds.length>0) {
          downLoadFileFromServer(link, params, 'Process-Task');
        }
      },

      onPrintButton() {
        let checkedAll = this.$refs.taskVuetable.checkedAllStatus;
        let checkedIds = this.$refs.taskVuetable.selectedTo;
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : false,
          'filter': this.filter,
          'idList': checkedIds.join()
        };
        let link = `task/process-task/generate`;
        if(checkedIds.length>0) {
          printFileFromServer(link, params);
        }
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
      onRowClicked: function (taskNumber) {

        this.pageStatus = 'show';
        let url1 = null;
        let url2 = null;
        let rateWidth, rateHeight;
        let imageInfo, rRectInfo;
        let colourInfo;
        // this.loadImage(url, url2);

        // call api
        getApiManager()
          .post(`${apiBaseUrl}/task/process-task/get-one`, {
            'taskId': taskNumber,
          })
          .then((response) => {
            let message = response.data.message;

            switch (message) {
              case responseMessages['ok']:
                this.showPage = response.data.data;
                this.apiBaseURL = apiBaseUrl;
                this.thumbs = [];
                this.videos = [];
                this.imgRect = [];
                this.cartoonRect = [];
                this.rRects = [];
                colourInfo = this.showPage.platFormCheckParams;
                //colourInfo = JSON.parse(colourInfo);
                if(this.showPage.serHandExamination!=null) {
                  this.validIcon = this.showPage.serHandExamination.handResult;
                }
                imageInfo = this.showPage.serScan.scanDeviceImages;
                imageInfo = JSON.parse(imageInfo);

                for(let i=0; i<imageInfo.length; i++){
                  url1=null;
                  url2=null;
                  rateWidth = 0;
                  rateHeight = 0;
                  if(imageInfo[i].image !=null) {
                    url1 = imageInfo[i].image;
                  }else{
                    url1 = '/assets/img/scan-lr.gif';
                  }
                  if(imageInfo[i].cartoon !=null) {
                    url2 = imageInfo[i].cartoon;
                  }else{
                    url2 = '/assets/img/u244.jpg';
                  }
                  this.imageUrl = url1;
                  this.cartoonUrl = url2;
                  console.log(this.cartoonUrl);
                  if(imageInfo[i].width !== 0 && imageInfo[i].height !== 0) {
                    rateWidth = 248 / imageInfo[i].width;
                    rateHeight = 521 / imageInfo[i].height;
                    for(let j=0; j<imageInfo[i].imageRects.length; j++) {
                      this.imgRect.push({
                        x: rateWidth * imageInfo[i].imageRects[j].x,
                        y: rateHeight * imageInfo[i].imageRects[j].y,
                        width: rateWidth * imageInfo[i].imageRects[j].width,
                        height: rateHeight * imageInfo[i].imageRects[j].height,
                        colour: colourInfo.scanRecogniseColour,
                      });
                    }
                    // this.imgRect[i].x = rateWidth * imageInfo[i].imageRects[0].x;
                    // this.imgRect[i].y = rateHeight * imageInfo[i].imageRects[0].y;
                    // this.imgRect[i].width = rateWidth * imageInfo[i].imageRects[0].width;
                    // this.imgRect[i].height = rateHeight * imageInfo[i].imageRects[0].height;
                    for(let j=0; j<imageInfo[i].cartoonRects.length; j++){
                      this.cartoonRect.push({
                        x: rateWidth * imageInfo[i].cartoonRects[j].x,
                        y: rateHeight * imageInfo[i].cartoonRects[j].y,
                        width: rateWidth * imageInfo[i].cartoonRects[j].width,
                        height: rateHeight * imageInfo[i].cartoonRects[j].height,
                        colour: colourInfo.scanRecogniseColour,
                      });
                    }

                    // this.cartoonRect[i].x = rateWidth * imageInfo[i].cartoonRects[0].x;
                    // this.cartoonRect[i].y = rateHeight * imageInfo[i].cartoonRects[0].y;
                    // this.cartoonRect[i].width = rateWidth * imageInfo[i].cartoonRects[0].width;
                    // this.cartoonRect[i].height = rateHeight * imageInfo[i].cartoonRects[0].height;
                  }
                }
                if(this.showPage.serJudgeGraph!=null) {
                  rRectInfo = this.showPage.serJudgeGraph.judgeSubmitrects;
                  rRectInfo = JSON.parse(rRectInfo);
                  if(rateHeight!==0&&rateWidth!==0) {
                    for (let i = 0; i < rRectInfo[0].rectsAdded.length; i++) {
                      console.log(rRectInfo[0].rectsAdded.length);
                      this.rRects.push({
                        x: rateWidth * rRectInfo[0].rectsAdded[i].x,
                        y: rateHeight * rRectInfo[0].rectsAdded[i].y,
                        width: rateWidth * rRectInfo[0].rectsAdded[i].width,
                        height: rateHeight * rRectInfo[0].rectsAdded[i].height,
                        colour: colourInfo.judgeRecogniseColour,
                      });

                      // this.rRects[i].x = rateWidth * rRectInfo[0].rectsAdded[i].x;
                      // this.rRects[i].y = rateHeight * rRectInfo[0].rectsAdded[i].y;
                      // this.rRects[i].width = rateWidth * rRectInfo[0].rectsAdded[i].width;
                      // this.rRects[i].height = rateHeight * rRectInfo[0].rectsAdded[i].height;
                    }

                    for (let i = 0; i < rRectInfo[0].rectsDeleted.length; i++) {
                      this.rRects.push({
                        x: rateWidth * rRectInfo[0].rectsDeleted[i].x,
                        y: rateHeight * rRectInfo[0].rectsDeleted[i].y,
                        width: rateWidth * rRectInfo[0].rectsDeleted[i].width,
                        height: rateHeight * rRectInfo[0].rectsDeleted[i].height,
                        colour: colourInfo.displayDeleteSuspicionColour,
                      });
                      // this.rRects[i].x = rateWidth * rRectInfo[0].rectsDeleted[i - rRectInfo[0].rectsAdded.length].x;
                      // this.rRects[i].y = rateHeight * rRectInfo[0].rectsDeleted[i - rRectInfo[0].rectsAdded.length].y;
                      // this.rRects[i].width = rateWidth * rRectInfo[0].rectsDeleted[i - rRectInfo[0].rectsAdded.length].width;
                      // this.rRects[i].height = rateHeight * rRectInfo[0].rectsDeleted[i - rRectInfo[0].rectsAdded.length].height;
                    }
                  }
                }
                loadImageCanvas(url1, url1, this.imgRect, this.rRects, this.power);

                let handGoodsStr = this.showPage.serCheckResult.handGoods;
                let handAttactedStr = this.showPage.serCheckResult.handAttached;
                let handGood = handGoodsStr.split(",");
                let handAttached = handAttactedStr.split(",");
                let k=0;
                for(let i=0; i<handGood.length; i++){
                  for(let j=0; j<5; j++){
                    if(handGood[i] === this.handGoodDataCode[j]){
                      this.handGoodExpanded[k] = true;
                      this.handGoodDataCodeExpanded[k] = this.handGoodDataCode[j];
                      k++;
                    }

                  }
                }

                //getting media data from server.
                for (let i = 0; i < handAttached.length; i++) {
                  let iHandAttached = handAttached[i].split(".");
                  if (iHandAttached[1] === "png" || iHandAttached[1] === "jpg") {
                    this.thumbs.push({
                      name:iHandAttached[0],
                      src:handAttached[i]
                    });
                    this.images.push(handAttached[i]);
                   /* this.thumbs[k].name = iHandAttached[0];
                    this.thumbs[k].src = handAttached[i];
                    this.images[k] = handAttached[i];*/

                  }
                  else {
                    this.videos.push({
                      name:iHandAttached[0],
                      src:handAttached[i],
                      poster:'',//todo if client need to show different poster for each videos, should get its poster image from server.
                    });
                  }

                }

                break;// okay

            }
          })
          .catch((error) => {
          });

        this.history_id = taskNumber;
      },
      getDateTimeFormat2(dataTime) {
        return getDateTimeWithFormat(dataTime);
      },
      getDateTimeFormat(dataTime) {
        return getDateTimeWithFormat(dataTime, 'monitor');
      },

      onSearchButton() {
        this.$refs.taskVuetable.refresh();
      },
      onResetButton() {

        this.filter = {
          taskNumber: null,
          mode: null,
          status: null,
          fieldId: null,
          userName: null,
          startTime: null,
          endTime: null
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
        let temp;

        for (let i = 0; i < data.data.length; i++) {
          temp = data.data[i];
          transformed.data.push(temp);
        }

        return transformed

      },

      taskVuetableHttpFetch(apiUrl, httpOptions) { // customize data loading for table from server

        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          filter: this.filter,
          sort:httpOptions.params.sort,
          perPage: this.taskVuetableItems.perPage,
        });
      },
      onTaskVuetablePaginationData(paginationData) {
        this.$refs.taskVuetablePagination.setPaginationData(paginationData)
      },
      onTaskVuetableChangePage(page) {
        this.$refs.taskVuetable.changePage(page)
      }
    }
  }
</script>
