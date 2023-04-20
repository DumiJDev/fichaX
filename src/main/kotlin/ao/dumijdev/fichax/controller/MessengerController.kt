package ao.dumijdev.fichax.controller

import com.restfb.DefaultFacebookClient
import com.restfb.DefaultJsonMapper
import com.restfb.FacebookClient
import com.restfb.Parameter
import com.restfb.Version
import com.restfb.types.send.IdMessageRecipient
import com.restfb.types.send.Message
import com.restfb.types.send.SendResponse
import com.restfb.types.webhook.WebhookObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*

@RestController
class MessengerController() {

  @Value("\${access_token}")
  lateinit var pageAccessToken: String
    val secretApp = "80162d8f8055268f9ab8d7c822bdb934"


  @GetMapping("/")
  fun connect(
    @RequestParam("hub.verify_token") verifyToken: String?,
    @RequestParam("hub.mode") mode: String?,
    @RequestParam("hub.challenge") challenge: String?,
    response: ServerHttpResponse
  ): Mono<String> {
    if (verifyToken != "ramanujam" || mode != "subscribe") {
      response.statusCode = HttpStatus.FORBIDDEN
      return Mono.empty()
    }

    return Mono.justOrEmpty(challenge)

  }

  @PostMapping("/webhook")
  fun message(
    @RequestBody body: String?,
    @RequestHeader("x-hub-signature-256") signature: String?
  ): Mono<Any> {

    println(body)

    val payload = DefaultJsonMapper().toJavaObject(body, WebhookObject::class.java)

    if (payload.isPage) {
      for (item in payload.entryList) {
        for (messageItem in item.messaging) {
          if (messageItem != null) {
            // create a version 2.6 client
            val pageClient: FacebookClient = DefaultFacebookClient(pageAccessToken, Version.VERSION_10_0)

            val recipient = IdMessageRecipient(messageItem.sender.id)

            val message = Message("Just a simple text")

            println("Sending message")

            val resp: SendResponse = pageClient.publish(
              "me/messages", SendResponse::class.java,
              Parameter.with("recipient", recipient),  // the id or phone recipient
              Parameter.with("message", message)
            ) // one of the messages from above

            if (resp.isSuccessful)
              return Mono.just("ok")
          }
        }
      }
    }
    return Mono.empty()
  }
}
