<template>
    <ContentField>
        我的Bot
    </ContentField>
</template>

<script>
import ContentField from "../../../components/ContentField.vue";
import $ from 'jquery';
import {useStore} from 'vuex';
export default{
    components:{
        ContentField,
    },

    setup(){
        const store = useStore();
        // $.ajax({
        //     url : "http://127.0.0.1:3000/user/bot/add/",
        //     type : "POST",
        //     data:{
        //         title: "bot标题",
        //         description: "bot描述",
        //         content: "bot代码",
        //     },
        //     headers:{
        //         Authorization: "Bearer " + store.state.user.token,
        //     },
        //     success(resp){
        //         console.log(resp);
        //     },
        //     error(resp)
        //     {
        //         console.log(resp);
        //     }
        // });
        // $.ajax({
        //     url: "http://127.0.0.1:3000/user/bot/remove/",
        //     type: "POST",
        //     data:{
        //         bot_id: "1",
        //     },
        //     headers:{
        //         Authorization: "Bearer " + store.state.user.token,
        //     },
        //     success(resp){
        //         console.log(resp);
        //     },
        //     error(resp)
        //     {
        //         console.log(resp);
        //     }
        // });

        // $.ajax({
        //     url: "http://127.0.0.1:3000/user/bot/update/",
        //     type: "POST",
        //     data:{
        //         bot_id: "2",
        //         title: "新标题",
        //         description: "新描述",
        //         content:"新代码",
        //     },
        //     headers:{
        //         Authorization: "Bearer " + store.state.user.token,
        //     },
        //     success(resp){
        //         console.log(resp);
        //     },
        //     error(resp)
        //     {
        //         console.log(resp);
        //     }
        // });

        $.ajax({
            url: "http://127.0.0.1:3000/user/bot/getlist/",
            type: "Get",
            headers:{
                Authorization: "Bearer " + store.state.user.token,
            },
            success(resp){
                console.log(resp);
            },
            error(resp)
            {
                console.log(resp);
            }
        });
    }
}
</script>

<style scoped>

</style>