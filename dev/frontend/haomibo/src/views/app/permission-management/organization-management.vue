<template>
  <div>
    <b-row>
      <b-colxx xxs="12">
        <piaf-breadcrumb :heading="$t('menu.organization-management')"/>
        <div class="separator mb-5"></div>
      </b-colxx>
    </b-row>

    <b-tabs nav-class="separator-tabs ml-0 mb-5" content-class="tab-content" :no-fade="true">
      <b-tab :title="$t('permission-management.organization-table')">
        <b-row>
          <b-colxx xxs="12" lg="8">
            <b-card class="mb-4" :title="$t('pages.comments')">
              <h1>Hello</h1>
            </b-card>
          </b-colxx>
        </b-row>
      </b-tab>

      <b-tab :title="$t('permission-management.organization-structure')">
        <b-row>
          <b-colxx xxl="12">
            <h1>world</h1>
          </b-colxx>
        </b-row>
      </b-tab>
    </b-tabs>

  </div>
</template>
