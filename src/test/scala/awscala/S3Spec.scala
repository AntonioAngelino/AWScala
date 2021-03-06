package awscala

import awscala._, s3._

import org.slf4j._
import org.scalatest._
import org.scalatest.matchers._

class S3Spec extends FlatSpec with ShouldMatchers {

  behavior of "S3"

  val log = LoggerFactory.getLogger(this.getClass)

  it should "provide cool APIs" in {
    implicit val s3 = S3.at(Region.Tokyo)

    // buckets
    val buckets = s3.buckets
    log.info(s"Buckets: ${buckets}")

    val newBucketName = s"awscala-unit-test-${System.currentTimeMillis}"
    val bucket = s3.createBucket(newBucketName)
    log.info(s"Created Bucket: ${bucket}")

    // create/update objectes
    bucket.put("S3.scala", new java.io.File("src/main/scala/awscala/s3/S3.scala"))
    bucket.putAsPublicRead("S3.scala", new java.io.File("src/main/scala/awscala/s3/S3.scala"))
    bucket.put("S3Spec.scala", new java.io.File("src/test/scala/awscala/S3Spec.scala"))

    // get objects
    val s3obj: Option[S3Object] = bucket.get("S3.scala")
    log.info(s"Object: ${s3obj}")
    val summaries = bucket.objectSummaries
    log.info(s"Object Summaries: ${summaries}")

    // delete objects
    s3obj.foreach(o => bucket.delete(o))
    bucket.get("S3Spec.scala").map(_.destroy()) // working with implicit S3 instance

    bucket.destroy()
  }

}
