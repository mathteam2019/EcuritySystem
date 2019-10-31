<template>
<div>
  <b-row>
    <b-col cols="12">
      <b-img src="/assets/img/home_main_banner.png" fluid-grow class="shadow-sm" />
    </b-col>
  </b-row>
  <b-row class="mt-5">
    <b-col cols="5">
      <b-img src="/assets/img/home_bottom_banner.png" fluid-grow class="shadow-sm" />
    </b-col>
    <b-col cols="7" class="pt-5 pl-5">
      <b-row>
        <b-col cols="6 d-flex">
          <div style="padding: 36px; border-radius: 5px; background-color: #1782D4">
            <b-img src="/assets/img/rocket_icon.svg" style="width: 38px; height: 38px;" />
          </div>
          <div class="ml-5 mt-2">
            <h5 style="color: #1782d4;">{{$t('dashboard.article-title-1')}}</h5>
            <h7 style="color: #757575">{{$t('dashboard.article-extract-1')}}</h7>
          </div>
        </b-col>
        <b-col cols="6 d-flex">
          <div style="padding: 36px; border-radius: 5px; background-color: #1782D4">
            <b-img src="/assets/img/rocket_icon.svg" style="width: 38px; height: 38px;" />
          </div>
          <div class="ml-5 mt-2">
            <h5 style="color: #1782d4;">{{$t('dashboard.article-title-2')}}</h5>
            <h7 style="color: #757575">{{$t('dashboard.article-extract-2')}}</h7>
          </div>
        </b-col>
      </b-row>
      <b-row class="mt-5">
        <b-col cols="6 d-flex">
          <div style="padding: 36px; border-radius: 5px; background-color: #1782D4">
            <b-img src="/assets/img/rocket_icon.svg" style="width: 38px; height: 38px;" />
          </div>
          <div class="ml-5 mt-2">
            <h5 style="color: #1782d4;">{{$t('dashboard.article-title-3')}}</h5>
            <h7 style="color: #757575">{{$t('dashboard.article-extract-3')}}</h7>
          </div>
        </b-col>
        <b-col cols="6 d-flex">
          <div style="padding: 36px; border-radius: 5px; background-color: #1782D4">
            <b-img src="/assets/img/rocket_icon.svg" style="width: 38px; height: 38px;" />
          </div>
          <div class="ml-5 mt-2">
            <h5 style="color: #1782d4;">{{$t('dashboard.article-title-4')}}</h5>
            <h7 style="color: #757575">{{$t('dashboard.article-extract-4')}}</h7>
          </div>
        </b-col>
      </b-row>
    </b-col>
  </b-row>
  </div>
</template>
