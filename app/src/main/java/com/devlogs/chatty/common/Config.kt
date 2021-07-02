package com.devlogs.chatty.common

const val PROTOCOL_TYPE = "http"
const val HOST = "10.0.2.2"
const val MAIN_PORT = 3000
const val AUTH_PORT = 4000

const val MAIN_SERVER_START_PATH = "$PROTOCOL_TYPE://$HOST:$MAIN_PORT"
const val AUTH_SERVER_START_PATH = "$PROTOCOL_TYPE://$HOST:$AUTH_PORT"

const val DOWNLOAD_AVATAR_GET_REQ_WITHOUT_PARAMS = "user/avatar/download"
fun getFullDownloadAvatarUrl (email: String) = "$MAIN_SERVER_START_PATH/$DOWNLOAD_AVATAR_GET_REQ_WITHOUT_PARAMS/$email"
const val CREATE_NEW_USER_POST_REQ = "user/newuser"
const val CHANGE_USER_AVATAR_POST_REQ = "user/changeUserAvatar"
const val CREATE_NEW_CHANNEL_POST_REQ = "channel/newchannel"
const val SEND_TEXT_MESSAGE_POST_REQ_WITHOUT_PARAMS = "message/sendtextmessage"
fun getFullSendTextMessagePostReq (channelId: String) = "$MAIN_SERVER_START_PATH/$SEND_TEXT_MESSAGE_POST_REQ_WITHOUT_PARAMS/$channelId"
const val SEND_RESOURCE_MESSAGE_POST_REQ_WITHOUT_PARAMS = "message/sendresourcemessage"
fun getFullSendSrcMessagePostReq (channelId: String) = "$MAIN_SERVER_START_PATH/$SEND_RESOURCE_MESSAGE_POST_REQ_WITHOUT_PARAMS/$channelId"
const val GET_RESOURCE_REQ = "resource/download"
fun getFullResourceDownloadUrl (resourceName: String) = "$MAIN_SERVER_START_PATH/$GET_RESOURCE_REQ/$resourceName"
const val UPLOAD_IMAGE_STORY_POST_REQ_WITHOUT_PARAMS = "story/upload"
fun getFullUploadImageStoryPostReq (channelId: String) = "$MAIN_SERVER_START_PATH/$UPLOAD_IMAGE_STORY_POST_REQ_WITHOUT_PARAMS/$channelId"

const val GENERATE_ACCESS_TOKEN_GET_REQ = "auth/token/generateaccesstoken"
const val REGISTER_POST_REQ = "auth/register"
const val LOGIN_POST_REQ = "auth/login"

