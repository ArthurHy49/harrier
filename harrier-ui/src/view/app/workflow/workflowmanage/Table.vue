<template>
  <div>
    <div class="spdb-form">
      <Form ref="workflowManageForm" :model="formBean" :label-width="80" :rules='ruleValidate'>
        <Row>
          <Col span="3">
            <Form-Item prop="platform" label="平台名" >
              <Select filterable v-model="formBean.platform" @on-change="querySystem" clearable>
                <Option v-for="item in platformData" :value="item.value" :key="item.value" :label="item.value">{{ item.label }}</Option>
              </Select>
            </Form-Item>
          </Col>
          <Col span="3">
            <Form-Item prop="systems" label="应用名" >
              <Select ref="refsystem" v-model="formBean.systems" style="width:100px" filterable  clearable>
                <Option v-for="item in systemData" :value="item.value" :key="item.value" :label="item.value">{{ item.label }}</Option>
              </Select>
            </Form-Item>
          </Col>
          <Col span="6">
            <Form-Item prop="job" label="作业名" >
              <Input  v-model="formBean.job"/>
            </Form-Item>
          </Col>
          <Col span="3">
            <Form-Item prop="etlSystem" label="版本号">
              <Input  v-model="formBean.version"/>
            </Form-Item>
          </Col>
        </Row>
        <Row>
           <Col span="10">
            <div class="spdb-toolbar">
              <Button type="primary" icon="ios-search" @click="search">查询</Button>
              <Button type="primary" icon="md-close" @click="clear">清除查询</Button>
              <Button icon="ios-cloud-upload-outline" @click="versionOp.uploadShow=true">导入工作流</Button>
              <!-- <S-AuthButton type="primary" icon="md-add" @click="add" :requestConfig=request.addReq>新增</S-AuthButton>
              <S-AuthButton type="primary" :loading="loadingdel" :disabled="ctrlDisable" icon="ios-trash-outline" @click="del()">删除</S-AuthButton> -->
              <!-- <Button type="primary" :loading="loadingdel" @click="">导入工作流</Button> -->
              <!-- <Button type="primary" :loading="loadingsync" icon="ios-sync" @click="sync">同步</Button> -->
            </div>
          </Col>
        </Row>
      </Form>
    </div>

		<div class="spdb-table">
			<Table :columns="gridColumns" :data="gridData" stripe @on-selection-change="select">
			</Table>
		</div>

		<div class="spdb-page">
			<Page placement="top" :total="page.total" :current="page.current" :page-size="page.size" :page-size-opts="page.opt" show-total show-sizer show-elevat @on-change="changePage" @on-page-size-change="changePageSize"></Page>
		</div>

    <Modal id="versionOp" v-model="versionOp.show" title="历史版本" width="50%" :mask-closable="false" :closable="false" >
      <div>
        <div>
          <Row>
            <Col span="2">
              <h3>版本号：</h3>
            </Col>
            <Col span="22">
              <Tag v-for="item in versionOp.data" style="margin-right:3%" :key="item" color="primary" :name="item" @click.native="versionDetail(item)" size="large">{{ item }}</Tag>
            </Col>
          </Row>
        </div>
      </div>
      <div slot="footer">
        <div class="spdb-toolbar">
          <!-- <Button icon="md-add" type="primary" @click="">{{ loadingStatus ? "Uploading" : "导入" }}</Button> -->
          <Button icon="md-close" type="primary" @click="versionOp.show=false" > 取消 </Button>
        </div>
      </div>
    </Modal>

    <Modal id="uploadOp" v-model="versionOp.uploadShow" title="工作流压缩包上传" width="50%" :mask-closable="false" :closable="false" >
      <div>
        <div>
          <Form ref="importOpForm" :label-width="100" >
            <Row>
              <Col span="16">
                <FormItem label="导入文件名:" style="cursor:pointer;" >
                  <Upload ref="importTar" name="files" :before-upload="handleUpload" action="" >
                    <Input style="width:400px;" type="textarea" :format="['tar']" :autosize="{minRows: 1,maxRows: 5}" v-model="files.length === 0? '' : files[0].name" placeholder="请选择文件"/>
                  </Upload>
                </FormItem>
              </Col>
              <Col span="8">
                <div class="spdb-toolbar">
                  <Button icon="md-add" type="primary" @click="importTar" :loading="loadingStatus" :disabled="loadingStatus" >{{ loadingStatus ? "Uploading" : "导入" }}</Button>
                  <Button icon="md-close" type="primary" @click="versionOp.uploadShow=false" > 取消 </Button>
                </div>
              </Col>
            </Row>
          </Form>
        </div>
      </div>
      <div slot="footer"></div>
    </Modal>
	</div>
</template>

<script>
import utils from '@/common/utils'

const RESOURCE_PATH = '/jobarrange'

export default {
  props: {
    transData: {
      type: Object,
      default: function () {
    			return {}
      }
    }
  },
  data: function () {
    return {
      platformData: [],
      systemData: [],
      ctrlDisable: true,
      loadingdel: false,
      loadingsync: false,
      loadingStatus: false,
      fileType: '',
      files: [],
      formBean: {},
      platformList: [],
      request: {
        delReq: {
          method: 'DELETE',
          url: RESOURCE_PATH
        },
        addReq: {
          method: 'POST',
          url: RESOURCE_PATH
        }
      },
      gridColumns: [
        {
          type: 'selection',
          width: 60,
          align: 'center'
          // fixed: 'left'
        },
        // {
        //   title: '序号',
        //   key: 'id',
        //   align: 'center',
        //   width: 80
        // },
        {
          title: '平台名',
          key: 'platform',
          align: 'center',
          width: 80
        },
        {
          title: '应用名',
          key: 'systems',
          align: 'center',
          width: 80
        },
        {
          title: '作业名',
          key: 'job',
          align: 'center',
          minWidth: 180
        },
        {
          title: '变更状态',
          key: 'job',
          align: 'taskStatus',
          width: 95,
          render: (h, { column, index, row }) => {
            return h('div', [
              h('Span', {}, this.taskStatusList[row.taskStatus])
            ])
          }
        },
        {
          title: '任务状态',
          key: 'processStatus',
          align: 'center',
          width: 95,
          render: (h, { column, index, row }) => {
            return h('div', [
              h('Span', {}, this.processStatusList[row.processStatus])
            ])
          }
        },
        {
          title: '同步状态',
          key: 'syncStatus',
          align: 'center',
          minWidth: 130,
          render: (h, { column, index, row }) => {
            return h('div', [
              h('Span', {}, this.syncStatusList[row.syncStatus])
            ])
          }
        },
        // {
        //   title: '进入Dispatcher时间',
        //   key: 'startDate',
        //   align: 'center',
        //   width: 180
        // },
        // {
        //   title: '开始执行时间',
        //   key: 'endDate',
        //   align: 'center',
        //   minWidth: 180
        // },
        {
          title: '版本号',
          key: 'version',
          align: 'center',
          minWidth: 120
        },
        {
          title: '操作',
          align: 'center',
          width: 300,
          // fixed: 'right',
          render: (h, { column, index, row }) => {
            // row.taskStatus 1->新增；2->变更；3->下线 4->上线完成
            // row.taskStatus = 2
            let col = '#03399b'
            let text = '上线'
            let iconStyle = 'md-arrow-round-up'
            let disable = false
            if (row.processStatus === 3) {
              col = 'red'
              text = '下线'
              iconStyle = 'md-arrow-round-down'
              disable = true
            } else {
              col = 'green'
            }

            return h('div', [
              h('Poptip', {
                props: {
                  content: '编辑',
                  transfer: true,
                  width: '50px',
                  trigger: 'hover'

                },
                class: {
                  poptipContentInner: true
                }
              }, [
                h('Button', { // 编辑
                  props: {
                    icon: 'md-create',
                    disabled: disable
                  },
                  style: {
                    marginRight: '5px',
                    margin: '0px',
                    fontSize: '16px',
                    border: 0,
                    padding: '4px 8px',
                    color: '#03399b',
                    background: 'transparent'
                  },
                  on: {
                    click: () => {
                      let queryCache = { formBean: this.formBean, currentPage: this.page.current, pageSize: this.page.size }
                      this.$emit('switch', Object.assign({}, { row: row, id: row.id }, queryCache)) // 提交form事件，在parent中显示form
                    }
                  }
                }, '')
              ]),
              h('Poptip', {
                props: {
                  content: text,
                  transfer: true,
                  width: '50px',
                  trigger: 'hover'
                },
                class: {
                  poptipContentInner: true
                }
              }, [
                h('Button', { // 上线：部署的流程（点完之后变为上线
                  props: {
                    icon: iconStyle
                  },
                  style: {
                    marginRight: '5px',
                    margin: '0px',
                    fontSize: '16px',
                    border: 0,
                    padding: '4px 8px',
                    color: col,
                    background: 'transparent'
                  },
                  on: {
                    click: () => {
                    // deploy 上线 rollback 下线
                      let params = Object.assign({}, row)
                      if (row.processStatus === 3) {
                        this.$Modal.confirm({
                          title: '提示',
                          content: '确定下线' + params.job + '作业吗',
                          onOk: () => {
                            let loadConfig = {
                              method: 'POST',
                              url: '/jobdevelop/rollback',
                              data: params
                            }
                            this.$ajax(loadConfig)
                              .then(resp => {
                                this.search()
                              })
                          },
                          onCancel: () => {
                          }
                        })
                      } else {
                        this.$Modal.confirm({
                          title: '提示',
                          content: '确定上线' + params.job + '作业吗',
                          onOk: () => {
                            let loadConfig = {
                              method: 'POST',
                              url: '/jobdevelop/deploy',
                              data: params
                            }
                            this.$ajax(loadConfig)
                              .then(resp => {
                                this.search()
                              })
                          },
                          onCancel: () => {
                          }
                        })
                      }
                    }
                  }
                }, '')
              ]),
              h('Poptip', {
                props: {
                  content: '删除',
                  transfer: true,
                  width: '50px',
                  trigger: 'hover'
                },
                class: {
                  poptipContentInner: true
                }
              }, [
                h('Button', { // 删除：只删除dy_job_arrange
                  props: {
                    icon: 'md-trash',
                    disabled: disable
                  // shape: 'circle',
                  // type: 'primary'
                  },
                  style: {
                    marginRight: '5px',
                    margin: '0px',
                    fontSize: '16px',
                    border: 0,
                    padding: '4px 8px',
                    color: '#03399b',
                    background: 'transparent'
                  },
                  on: {
                    click: () => {
                      this.$Modal.confirm({
                        title: '提示',
                        content: '确定删除' + row.job + '作业吗',
                        onOk: () => {
                          let delConfig = {
                            method: 'DELETE',
                            url: RESOURCE_PATH + '/delete/' + row.id
                          }
                          this.$ajax(delConfig)
                            .then(resp => {
                              this.search()
                            })
                        },
                        onCancel: () => {
                        }
                      })
                    }
                  }
                }, '')
              ]),
              h('Poptip', {
                props: {
                  content: '导出',
                  transfer: true,
                  width: '50px',
                  trigger: 'hover'
                },
                class: {
                  poptipContentInner: true
                }
              }, [
                h('Button', { // 导出：下载一个压缩包
                  props: {
                    icon: 'md-redo'
                  // shape: 'circle',
                  // type: 'primary'
                  },
                  style: {
                    marginRight: '5px',
                    margin: '0px',
                    fontSize: '16px',
                    border: 0,
                    padding: '4px 8px',
                    color: '#03399b',
                    background: 'transparent'
                  },
                  on: {
                    click: () => {
                      let params = { platform: row.platform, systems: row.systems, job: row.job, version: row.version }
                      let delConfig = {
                        method: 'GET',
                        url: RESOURCE_PATH + '/download',
                        params: params
                      }
                      this.$ajax(delConfig)
                        .then(resp => {
                          utils.blobDownload(resp)
                        })
                    }
                  }
                }, '')
              ]),
              h('Poptip', {
                props: {
                  content: '版本切换',
                  transfer: true,
                  width: '50px',
                  trigger: 'hover'
                },
                class: {
                  poptipContentInner: true
                }
              }, [
                h('Button', { // 版本切换
                  props: {
                    icon: 'md-repeat',
                    disabled: disable
                  // shape: 'circle',
                  // type: 'primary'
                  },
                  style: {
                    marginRight: '5px',
                    margin: '0px',
                    fontSize: '16px',
                    border: 0,
                    padding: '4px 8px',
                    color: '#03399b',
                    background: 'transparent'
                  },
                  on: {
                    click: () => {
                      let params = { platform: row.platform, systems: row.systems, job: row.job, version: row.version }
                      let delConfig = {
                        method: 'GET',
                        url: RESOURCE_PATH + '/loadJobArrangesByVersion',
                        params: params
                      }
                      this.$ajax(delConfig)
                        .then(resp => {
                          console.log(resp)
                        })
                    }
                  }
                }, '')
              ]),
              h('Poptip', {
                props: {
                  content: '历史版本',
                  transfer: true,
                  width: '50px',
                  trigger: 'hover'
                },
                class: {
                  poptipContentInner: true
                }
              }, [
                h('Button', { // 历史版本
                  props: {
                    icon: 'md-search'
                  // shape: 'circle',
                  // type: 'primary'
                  },
                  style: {
                    marginRight: '5px',
                    margin: '0px',
                    fontSize: '16px',
                    border: 0,
                    padding: '4px 8px',
                    color: '#03399b',
                    background: 'transparent'
                  },
                  on: {
                    click: () => {
                      let params = { platform: row.platform, systems: row.systems, job: row.job }
                      let delConfig = {
                        method: 'GET',
                        url: RESOURCE_PATH + '/versionCodeList/',
                        params: params
                      }
                      this.$ajax(delConfig)
                        .then(resp => {
                          this.versionOp.platform = row.platform
                          this.versionOp.systems = row.systems
                          this.versionOp.job = row.job
                          this.versionOp.show = true
                          this.versionOp.data = resp.data
                        })
                    }
                  }
                }, '')
              ])

            ])
          }
        }
      ],
      versionOp: {
        platform: '',
        systems: '',
        job: '',
        show: false,
        uploadShow: false,
        data: [],
        detail: {}
      },
      gridData: [],
      selection: [],
      page: {
        current: 1,
        total: 10,
        size: 10,
        opt: [10, 50, 100]
      },
      ruleValidate: {
      },
      syncStatusList: { '0': '正常线上开发无需同步', '1': '成功', '2': '失败' },
      processStatusList: { '1': '待投产', '2': '投产失败', '3': '投产成功' },
      taskStatusList: { '1': '新增', '2': '变更', '3': '下线' }
    }
  },
  watch: {
    selection: function (val) {
      if (val.length != 0) {
        this.ctrlDisable = false
      } else {
        this.ctrlDisable = true
      }
    }
  },
  methods: {
    versionDetail (version) {
      let httpConfig = {
        method: 'GET',
        url: RESOURCE_PATH + '/loadJobArrangesByVersion',
        params: { platform: this.versionOp.platform, systems: this.versionOp.systems, job: this.versionOp.job, version: version }
      }
      this.$ajax(httpConfig)
        .then(resp => {
          if (resp.status && resp.status === 200) {
            this.gridData = []
            this.gridData.push(resp.data)
            this.page.total = 1
            // this.versionOp.detail = resp.data
            this.versionOp.show = false
          }
        })
    },
    // 同步
    sync () {
      // 同步
      let loadConfig = {
        method: 'GET',
        url: RESOURCE_PATH + '/sync'
      }
      this.loadingsync = true
      this.$ajax(loadConfig)
        .then(resp => {
          this.loadingsync = false
          if (resp.data == '1') {
            this.$Notice.success({
              title: '信息',
              desc: '同步成功'
            })
          } else {
            this.$Notice.error({
              title: '信息',
              desc: '同步失败'
            })
          }
          this.search()
        })
    },
    queryPlatform () {
      let platformList = this.$store.getters.getUserPlatform()
      platformList.forEach(data => {
        let tmp = {}
        tmp.value = data
        tmp.label = data
        this.platformData.push(tmp)
      })
    },
    querySystem () {
      this.systemData = []
      this.$refs.refsystem.setQuery('')
      if (this.formBean.platform) {
        let userSystemList = this.$store.getters.getUserSystem()
        let userSystem = userSystemList[this.formBean.platform]
        userSystem.forEach(data => {
          let tmp = {}
          tmp.value = data
          tmp.label = data
          this.systemData.push(tmp)
        })
      }
    },
    /**
		 * 初始化
		 **/
    init () {
      if (this.transData.job) {
        this.formBean.job = this.transData.job
      }
      if (this.transData.currentPage) {
        this.page.current = this.transData.currentPage
        this.page.size = this.transData.pageSize
      }
      this.search()
    },
    /**
         * 如果传过来的有初始数据则进行数据绑定
         **/
    bindData (fields) {
      try {
        const data = Object.assign({}, this.transData.initFormBean)
        if (fields) { // 进行可选字段初始化
          for (const field of fields) {
            this.formBean[field] = data[field]
          }
        } else { // 进行全量字段初始化（默认是只有主表的主键的）
          this.formBean = Object.assign({}, data)
        }
      } catch (error) {
        console.error(error)
      }
    },
    /**
		 * 下载
		 **/
    download () {
      let params = {}
      Object.assign(params, this.formBean)
      params.type = 'excel'
      utils.download(RESOURCE_PATH + '/downLoad', params)
    },
    /**
		 * 查询
		 **/
    search () {
      let params = {}
      Object.assign(params, this.formBean)
      params.current = this.page.current
      params.size = this.page.size
      if (this.formBean.job) {
        params.currentPage = 1
      }
      let httpConfig = {
        method: 'GET',
        url: RESOURCE_PATH + '/findDjAgAll',
        params: params
      }

      this.$ajax(httpConfig)
        .then(resp => {
          this.gridData = resp.data.records
          this.page.total = resp.data.total
        })
    },
    /**
		 * 清空
		 **/
    clear () {
      this.formBean = {}
      this.selection = []
      this.page = {
        current: 1,
        size: 10
      }
    },
    /**
		 * 新增
		 **/
    add () {
      this.$emit('switch')
    },
    /**
		 * 删除
		 **/
    del () {
      const ids = Array.from(this.selection, e => e.alarmsettingid)
      ids.forEach(id => {
        this.request.delReq.url = RESOURCE_PATH + '/' + id
        this.request.delReq.method = 'DELETE'
        this.loadingdel = true
        this.$ajax(this.request.delReq)
          .then(resp => {
            this.loadingdel = false
            this.search()
          })
      })
    },
    handleUpload: function (file) {
      var fileExtension = (file.name).match(/\.([0-9a-z]+)(?:[\?#]|$)/i)
      if (fileExtension.pop() !== 'tar') {
        this.$Message.warning('您上传的不是工程压缩包！')
        return false
      }
      if (!this.multiple) {
        if (this.files.length > 0) {
          this.fileError = `不能选择多个的文件`
          return false
        }
      }
      if (this.files.find(e => e.name == file.name)) {
        this.fileError = `${file.name}已存在上传列表，已自动过滤掉！`
        return false
      }
      this.files.push(file)
      return false
    },
    importTar () {
      var formData = new FormData()
      formData.append('fileType', 'tar')
      if (this.files.length == 0 || this.files == null) {
        this.$Message.warning('请选择导入工程tar包')
      } else {
        for (const ele of this.files) {
          formData.append('file', ele)
        }
        this.loadingStatus = true
        this.$ajax.post('/jobattributes/parseProject', formData)
          .then(resp => {
            this.loadingStatus = false
            this.files = []
            this.versionOp.uploadShow = false
            this.search()
          })
      }
      // })
    },
    /**
		 * 数据复选事件
		 **/
    select (selection) {
      this.selection = selection
    },
    getPageParam () {
      return { formBean: this.alarmBean,
        currentPage: this.page.current,
        pageSize: this.page.size }
    },
    /**
		 * 改变页码事件
		 **/
    changePage (currentPage) {
      // alert(currentPage);
      this.page.current = currentPage
      this.search()
    },
    /**
		 * 改变分页大小事件
		 **/
    changePageSize (pageSize) {
      this.page.size = pageSize
      this.search()
    }
  },
  /**
	 * 视图挂载
	 **/
  mounted () {
    this.init()
    this.queryPlatform()
  }
}
</script>
<style>
.ivu-poptip-body-content-inner {
    text-align: center;
    /* overflow: auto; */
    width: 100%;
    word-break: break-word;
}
</style>
<style scoped>
.detail-title{
  font-size: 15px;
}
.detail-message {
  color: #3e3f4b;
  margin-left: 10px;
  font-size: 13px;
  font-weight: bold;
}
</style>
