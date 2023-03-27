<template>
    <div class="container">
        <div class="row">
            <div class="col-3">
                <div class="card" style="margin-top: 20px;">
                    <div class="card-body">
                        <img :src="$store.state.user.photo" alt="" style="width:100%;">
                    </div>
                </div>
            </div>
            <div class="col-9">
                <div class="card" style="margin-top:20px;">
                    <div class="card-header">
                        <span style="font-size: 130%;">创建bot</span>
                        <button type="button" class="btn btn-primary float-end" data-bs-toggle="modal" data-bs-target="#add-bot-btn">
                            创建bot
                        </button>
                        <!-- Modal add_bot -->
                        <div class="modal fade" id="add-bot-btn" tabindex="-1">
                            <div class="modal-dialog modal-xl">
                                <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title" id="exampleModalLabel">创建bot</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                </div>
                                <div class="modal-body">
                                    <form>
                                        <div class="mb-3">
                                            <label for="add-bot-title" class="form-label">名称</label>
                                            <input v-model="botadd.title" type="text" class="form-control" id="add-bot-title" aria-describedby="emailHelp" placeholder="请输入bot名称">
                                        </div>
                                        <div class="mb-3">
                                            <label for="add-bot-description" class="form-label">bot简介</label>
                                            <input v-model="botadd.description" type="text" class="form-control" id="add-bot-description" placeholder="请输入bot的简介">
                                        </div>
                                        <div class="mb-3">
                                            <label for="add-bot-code" class="form-label">code</label>
                                            <VAceEditor v-model:value="botadd.content"
                                                @init="editorInit" lang="c_cpp"
                                                :theme="aceConfig.theme" style="height: 300px"
                                                :options="aceConfig.options" class="ace-editor" />
                                        </div>
                                    </form>
                                </div>
                                <div class="modal-footer">
                                    <div class="error-message">{{ botadd.error_message }}</div>
                                    <button @click="add_bot" type="button" class="btn btn-primary">创建</button>
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                                </div>
                                </div>
                            </div>
                        </div>

                    </div>
                    <div class="card-body">
                        <table class="table table-striped table-hover">
                        <thead>
                            <tr>
                                <th>名称</th>
                                <th>创建时间</th>
                                <th>操作</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="bot in bots" :key="bot.id">
                                <td>{{ bot.title }}</td>
                                <td>{{ bot.createTime }}</td>
                                <td>
                                    <button type="button" class="btn btn-secondary" style="margin-right: 10px;" data-bs-toggle="modal" :data-bs-target="'#update-bot-modal-'+bot.id">修改</button>
                                    <button type="button" class="btn btn-danger"  data-bs-toggle="modal" :data-bs-target="'#remove-bot-modal-'+bot.id">删除</button>

                                    <!-- Modal update-->
                                    <div class="modal fade" :id="'update-bot-modal-'+bot.id" tabindex="-1">
                                        <div class="modal-dialog modal-xl">
                                            <div class="modal-content">
                                            <div class="modal-header">
                                                <h5 class="modal-title">创建bot</h5>
                                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                            </div>
                                            <div class="modal-body">
                                                <form>
                                                    <div class="mb-3">
                                                        <label for="add-bot-title" class="form-label">名称</label>
                                                        <input v-model="bot.title" type="text" class="form-control" id="add-bot-title" aria-describedby="emailHelp" placeholder="请输入bot名称">
                                                    </div>
                                                    <div class="mb-3">
                                                        <label for="add-bot-description" class="form-label">bot简介</label>
                                                        <input v-model="bot.description" type="text" class="form-control" id="add-bot-description" placeholder="请输入bot的简介">
                                                    </div>
                                                    <div class="mb-3">
                                                        <label for="add-bot-code" class="form-label">code</label>
                                                        <VAceEditor v-model:value="bot.content"
                                                                        @init="editorInit" lang="c_cpp"
                                                                        :theme="aceConfig.theme" style="height: 300px"
                                                                        :options="aceConfig.options" class="ace-editor" />
                                                    </div>
                                                </form>
                                            </div>
                                            <div class="modal-footer">
                                                <div class="error-message">{{ bot.error_message }}</div>
                                                <button @click="update_bot(bot)" type="button" class="btn btn-primary">保存修改</button>
                                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                                            </div>
                                            </div>
                                        </div>
                                    </div>
                                    <!-- Modal remove-->
                                    <div class="modal fade" :id="'remove-bot-modal-' + bot.id" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
                                        <div class="modal-dialog">
                                            <div class="modal-content">
                                            <div class="modal-header">
                                                <h5 class="modal-title">提醒</h5>
                                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                            </div>
                                            <div class="modal-body">
                                                确认要删除bot吗？
                                            </div>
                                            <div class="modal-footer">
                                                <button type="button" class="btn btn-primary" @click="remove_bot(bot)">确定</button>
                                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                                            </div>
                                            </div>
                                        </div>
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { ref ,reactive } from 'vue';
import $ from 'jquery';
import { useStore } from 'vuex';
import { Modal } from 'bootstrap/dist/js/bootstrap';
import ace from 'ace-builds';
import { VAceEditor } from "vue3-ace-editor";
import "ace-builds/webpack-resolver";
import 'ace-builds/src-noconflict/mode-json';
import 'ace-builds/src-noconflict/theme-chrome';
import 'ace-builds/src-noconflict/ext-language_tools';
export default{
    components:{
        VAceEditor,
    },
    setup(){
        //配置ace-editor
        ace.config.set(
            "basePath", 
            "https://cdn.jsdelivr.net/npm/ace-builds@" + require('ace-builds').version + "/src-noconflict/")
        function editorInit() {
            require("ace-builds/src-noconflict/ext-language_tools");
            require("ace-builds/src-noconflict/snippets/sql");
            require("ace-builds/src-noconflict/mode-sql");
            require("ace-builds/src-noconflict/theme-monokai");
            require("ace-builds/src-noconflict/mode-html");
            require("ace-builds/src-noconflict/mode-html_elixir");
            require("ace-builds/src-noconflict/mode-html_ruby");
            require("ace-builds/src-noconflict/mode-javascript");
            require("ace-builds/src-noconflict/mode-python");
            require("ace-builds/src-noconflict/snippets/less");
            require("ace-builds/src-noconflict/theme-chrome");
            require("ace-builds/src-noconflict/ext-static_highlight");
            require("ace-builds/src-noconflict/ext-beautify");
        }
        const aceConfig = reactive({
            theme: 'chrome', //主题
            arr: [
                /*所有主题*/
                "ambiance",
                "chaos",
                "chrome",
                "clouds",
                "clouds_midnight",
                "cobalt",
                "crimson_editor",
                "dawn",
                "dracula",
                "dreamweaver",
                "eclipse",
                "github",
                "gob",
                "gruvbox",
                "idle_fingers",
                "iplastic",
                "katzenmilch",
                "kr_theme",
                "kuroir",
                "merbivore",
                "merbivore_soft",
                "monokai",
                "mono_industrial",
                "pastel_on_dark",
                "solarized_dark",
                "solarized_light",
                "sqlserver",
                "terminal",
                "textmate",
                "tomorrow",
                "tomorrow_night",
                "tomorrow_night_blue",
                "tomorrow_night_bright",
                "tomorrow_night_eighties",
                "twilight",
                "vibrant_ink",
                "xcode",
            ],
            readOnly: false, //是否只读
            options: {
                enableBasicAutocompletion: true,
                enableSnippets: true,
                enableLiveAutocompletion: true,
                tabSize: 2,
                showPrintMargin: false,
                fontSize: 16
            }
        });

        const store = useStore();
        let bots = ref([]);
        const botadd = reactive({
            title:"",
            description:"",
            content:"",
            error_message:"",
        });
        const refresh_bots = ()=>{
            $.ajax({
                url:"http://127.0.0.1:3000/user/bot/getlist/",
                type: "GET",
                headers:{
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp)
                {
                    bots.value = resp;
                },
                error(resp)
                {
                    console.log(resp);
                }
            });
        }
        
        refresh_bots();
        
        const add_bot = ()=>{
            botadd.error_message = "";
            $.ajax({
                url:"http://127.0.0.1:3000/user/bot/add/",
                type:"POST",
                data:{
                    title:botadd.title,
                    description:botadd.description,
                    content:botadd.content,
                },
                headers:{
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp)
                {
                    if(resp.error_message === "success")
                    {
                        botadd.title="";
                        botadd.description="";
                        botadd.content="";

                        Modal.getInstance("#add-bot-btn").hide();
                        refresh_bots();
                    }
                    else{
                        console.log(resp);
                        botadd.error_message = resp.error_message;
                    }
                }
            })
        }
        
        const remove_bot = (bot)=>{
            $.ajax({
                url:"http://127.0.0.1:3000/user/bot/remove/",
                type: "POST",
                headers:{
                    Authorization: "Bearer " + store.state.user.token,
                },
                data:{
                    bot_id:bot.id,
                },
                success(resp)
                {
                    if(resp.error_message === "success")
                    {
                        refresh_bots();
                    }
                    Modal.getInstance('#remove-bot-modal-' + bot.id).hide();

                },
                error(resp)
                {
                    console.log(resp);
                }
            });
        }

        const update_bot = (bot)=>{
            botadd.error_message = "";
            $.ajax({
                url:"http://127.0.0.1:3000/user/bot/update/",
                type:"POST",
                data:{
                    bot_id: bot.id,
                    title: bot.title,
                    description: bot.description,
                    content: bot.content,
                },
                headers:{
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp)
                {
                    if(resp.error_message === "success")
                    {
                        Modal.getInstance('#update-bot-modal-' + bot.id).hide();
                        refresh_bots();
                    }
                    else{
                        botadd.error_message = resp.error_message;
                    }
                }
            })
        }

        return{
            bots,
            botadd,
            aceConfig,
            add_bot,
            remove_bot,
            update_bot,
            editorInit,
        }
    }
}
</script>

<style scoped>
.error-message{
    color:red;
}
</style>