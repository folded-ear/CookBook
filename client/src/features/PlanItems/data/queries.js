import { gql } from "@apollo/client"

export const GetPlansQuery = gql`query{
  getPlans{
    id
    owner {
      name
    }
    name
    grants {
      user {
        name
      }
      level
    }
    buckets {
      name
    }
  }
}`;